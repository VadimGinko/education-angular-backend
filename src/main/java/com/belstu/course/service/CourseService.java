package com.belstu.course.service;

import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.mapper.CourseMapper;
import com.belstu.course.model.Course;
import com.belstu.course.model.Subscription;
import com.belstu.course.model.enums.ActivityStatus;
import com.belstu.course.model.enums.CourseStatus;
import com.belstu.course.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CourseTypeRepository courseTypeRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public void create(CourseDto courseDto, String userName) throws Exception {
        var user = userRepository.findByEmail(userName).orElseThrow(
                () -> new Exception(String.format("Пользователя с именем '%s' нет в системе", userName)));
        if (courseRepository.existsByName(courseDto.getName()))
            throw new Exception(String.format("курс с именем %s уже существует", courseDto.getName()));
        var courseType = courseTypeRepository.findByName(courseDto.getType().getName()).orElseThrow(
                () -> new Exception(String.format("Типа курса с именем '%s' не существует", courseDto.getType().getName())));
        Course course = Course
                .builder()
                .name(courseDto.getName())
                .description(courseDto.getDescription())
                .type(courseType)
                .status(CourseStatus.IN_ACTIVE)
                .publisher(user)
                .build();
        course.setCreatedOn(LocalDate.now());
        Course createdCourse = courseRepository.save(course);
    }

    public void edit(CourseDto courseDto) throws Exception {
        Course course = courseRepository.findById(courseDto.getId()).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", courseDto.getId())));
        var courseType = courseTypeRepository.findById(courseDto.getType().getId()).orElseThrow(
                () -> new Exception(String.format("Типа курса с id=%s не существует", courseDto.getType().getId())));
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setStatus(courseDto.getStatus());
        course.setType(courseType);
        courseRepository.save(course);
    }

    public CourseDto findById(Long courseId) throws Exception {
        CourseDto course = courseMapper.toDto(courseRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", courseId))));
        return sortTasksByOrder(course);
    }

    public CourseDto sortTasksByOrder(CourseDto course) {
        course.setTasks(course.getTasks().stream()
                .sorted(Comparator.comparingInt(TaskDto::getOrder))
                .collect(Collectors.toList()));
        return course;
    }

    public List<CourseDto> findByFilters(Long userId, CourseStatus status, String courseName, String courseTypeName) {
        Stream<Course> coursesStream = courseRepository.findByStatus(status).stream();
        if (courseName != null)
            coursesStream = coursesStream.filter(i -> i.getName().contains(courseName));
        if (courseTypeName != null)
            coursesStream = coursesStream.filter(i -> i.getType().getName().equals(courseTypeName));

        var userCourses = findUserCoursesIds(userId)
                .map(Course::getId)
                .toList();

        // удаляем курсы на которые подписан пользователь и высчитываем рейтинг курса
        return coursesStream
                .filter(i -> !userCourses.contains(i.getId()))
                .map(courseMapper::toDto)
                .map(this::setRating)
                .toList();
    }

    public Stream<Course> findUserCoursesIds(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .filter(i -> i.getStatus() == ActivityStatus.ACTIVE)
                .map(Subscription::getCourse);
    }

    public List<CourseDto> findAll() {
        return courseMapper.toDto(courseRepository.findAll());
    }

    public List<CourseDto> getTeacherCourses(Long publisherId) {
        return courseMapper.toDto(courseRepository.findByPublisherId(publisherId));
    }

    public CourseDto setRating(CourseDto course) {
        List<Subscription> subscriptions = subscriptionRepository.findByCourseId(course.getId());
        course.setRating(subscriptions.stream()
                .filter(i -> i.getRating() != 0)
                .mapToInt(Subscription::getRating)
                .average()
                .orElse(0));
        return course;
    }

    @Transactional
    public void removeById(Long courseId) throws Exception {
        List<Subscription> subscriptions = subscriptionRepository.findByCourseId(courseId);
        if (!subscriptions.isEmpty())
            throw new Exception(String.format("Невозможно удалить курс с id=%s, так как есть подписанные на него пользователи", courseId));
        courseRepository.deleteById(courseId);
    }
}
