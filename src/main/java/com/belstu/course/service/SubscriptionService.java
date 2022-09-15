package com.belstu.course.service;

import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.dto.subscription.SubscriptionDto;
import com.belstu.course.mapper.CourseMapper;
import com.belstu.course.mapper.SubscriptionMapper;
import com.belstu.course.model.Course;
import com.belstu.course.model.Subscription;
import com.belstu.course.model.TaskProgress;
import com.belstu.course.model.User;
import com.belstu.course.model.enums.ActivityStatus;
import com.belstu.course.model.enums.ProgressStatus;
import com.belstu.course.repository.CourseRepository;
import com.belstu.course.repository.SubscriptionRepository;
import com.belstu.course.repository.TaskProgressRepository;
import com.belstu.course.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final SubscriptionMapper subscriptionMapper;
    private final CourseMapper courseMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final TaskProgressRepository taskProgressRepository;

    public void create(Long userId, Long courseId) throws Exception {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserIdAndCourseId(userId, courseId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.setStatus(ActivityStatus.ACTIVE);
            subscriptionRepository.save(subscription);
        } else {
            Course course = courseRepository.findById(courseId).orElseThrow(
                    () -> new Exception(String.format("Курса с id=%s не существует", courseId)));
            User user = this.userRepository.findById(userId).orElseThrow(() ->
                    new Exception(String.format("Пользователя с id=%s не существует", userId)));
            //initialize progress of tasks with READY status
            course.getTasks().forEach(task ->
                    taskProgressRepository.save(
                            TaskProgress
                                    .builder()
                                    .task(task)
                                    .student(user)
                                    .status(ProgressStatus.READY)
                                    .build()
                    )
            );
            subscriptionRepository.save(
                    Subscription.builder()
                            .course(course)
                            .user(user)
                            .rating(0)
                            .status(ActivityStatus.ACTIVE)
                            .build()
            );
        }
    }

    public void update(Long userId, SubscriptionDto subscriptionDto) throws Exception {
        Subscription subscription = subscriptionRepository.findByUserIdAndCourseId(
                userId, subscriptionDto.getCourse().getId()).orElseThrow(
                () -> new Exception(String.format("Подписки для курса с id=%s у пользователя с id=%s не существует", subscriptionDto.getCourse().getId(), userId)));
        if (subscriptionDto.getStatus() != null)
            subscription.setStatus(subscriptionDto.getStatus());
        if (subscriptionDto.getRating() != null)
            subscription.setRating(subscriptionDto.getRating());
        subscriptionRepository.save(subscription);
    }

    public void remove(Long userId, Long courseId) throws Exception {
        Subscription subscription = subscriptionRepository.findByUserIdAndCourseId(
                userId, courseId).orElseThrow(
                () -> new Exception(String.format("Подписки для курса с id=%s у пользователя с id=%s не существует", courseId, userId)));
        subscriptionRepository.delete(subscription);
    }


    public List<CourseDto> getSubscribedCourses(Long userId) {
        return subscriptionRepository
                .findByUserId(userId).stream()
                .filter(i -> i.getStatus() == ActivityStatus.ACTIVE)
                .map(Subscription::getCourse)
                .map(courseMapper::toDto)
                .toList();
    }

    public SubscriptionDto getByCourseAndUserId(Long userId, Long courseId) throws Exception {
        SubscriptionDto subscriptionDto = subscriptionMapper.toDto(subscriptionRepository.findByUserIdAndCourseId(
                userId, courseId).orElseThrow(
                () -> new Exception(String.format("Подписки для курса с id=%s у пользователя с id=%s не существует", courseId, userId))));
        subscriptionDto.setCourse(courseService.sortTasksByOrder(subscriptionDto.getCourse()));
        return subscriptionDto;
    }
}
