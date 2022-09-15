package com.belstu.course.service;

import com.belstu.course.dto.ReviewDto;
import com.belstu.course.dto.taskProgress.CodeAnswerDto;
import com.belstu.course.dto.taskProgress.TaskProgressDto;
import com.belstu.course.mapper.TaskMapper;
import com.belstu.course.model.*;
import com.belstu.course.model.enums.ProgressStatus;
import com.belstu.course.model.enums.TaskType;
import com.belstu.course.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskProgressService {
    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;
    private final CodeAnswerRepository codeAnswerRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public void changeStatus(TaskProgressDto taskProgressDto) throws Exception {
        TaskProgress taskProgress = this.taskProgressRepository.findByStudentIdAndTaskId(taskProgressDto.getUserId(), taskProgressDto.getTaskId()).orElseThrow(() ->
                new Exception(String.format("Прогресса для задачи с id=%s для студента с id=%s не существует", taskProgressDto.getTaskId(), taskProgressDto.getUserId())));
        taskProgress.setStatus(taskProgressDto.getStatus());
        taskProgressRepository.save(taskProgress);
    }

    public void changeStatusByProgressId(TaskProgressDto taskProgressDto) throws Exception {
        TaskProgress taskProgress = this.taskProgressRepository.findById(taskProgressDto.getId()).orElseThrow(() ->
                new Exception(String.format("Прогресса задачи с id=%s не существует", taskProgressDto.getId())));
        taskProgress.setStatus(taskProgressDto.getStatus());
        taskProgressRepository.save(taskProgress);
    }

    public List<TaskProgressDto> getByCourseId(Long courseId, Long userId) throws Exception {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", courseId)));
        List<TaskProgressDto> progresses = new ArrayList<>();
        for (var task : course.getTasks()) {
            progresses.add(getByUserIdAndTaskId(userId, task.getId()));
        }
        return progresses;
    }

    public List<TaskProgressDto> getByCourseId2(Long courseId, Long userId) throws Exception {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", courseId)));
        List<TaskProgressDto> progresses = new ArrayList<>();
        for (var task : course.getTasks()) {
            progresses.add(getByUserIdAndTaskId2(userId, task.getId()));
        }
        return progresses;
    }

    public TaskProgressDto getByUserIdAndTaskId2(Long userId, Long taskId) {
        TaskProgress taskProgress = this.taskProgressRepository.findByStudentIdAndTaskId(userId, taskId).orElse(
                null
        );
        if(taskProgress == null){
            create(userId, taskId);
        }
        return TaskProgressDto.builder()
                .id(taskProgress.id)
                .taskId(taskId)
                .userId(userId)
                .status(taskProgress.getStatus())
                .build();
    }

    public TaskProgressDto getByUserIdAndTaskId(Long userId, Long taskId) {
        TaskProgress taskProgress = this.taskProgressRepository.findByStudentIdAndTaskId(userId, taskId).orElse(
                null
        );
        if(taskProgress == null){
            return null;
        }
        return TaskProgressDto.builder()
                .id(taskProgress.id)
                .taskId(taskId)
                .userId(userId)
                .status(taskProgress.getStatus())
                .build();
    }

    public TaskProgress create(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId).get();
        User user = this.userRepository.findById(userId).get();
        //initialize progress of tasks with READY status
        return taskProgressRepository.save(
                TaskProgress
                        .builder()
                        .task(task)
                        .student(user)
                        .status(ProgressStatus.READY)
                        .build()
        );
    }

    public void remove(Long courseId, Long studentId) throws Exception {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", courseId)));
        for (Task task : course.getTasks()) {
            TaskProgress progress = taskProgressRepository.findByStudentIdAndTaskId(studentId, task.getId()).orElse(null);
            if (progress == null) {
                continue;
            }
            if(task.getType().equals(TaskType.CODE)){
                var codeAnswer = codeAnswerRepository.findByTaskProgressId(progress.id).get();
                codeAnswerRepository.delete(codeAnswer);
            }
            taskProgressRepository.delete(progress);
        }
    }

    public List<ReviewDto> getTaskToReview(Long courseId, Long publisherId) throws Exception {
        Course course = courseRepository.findByIdAndPublisherId(courseId, publisherId).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s и создателем сайта с id=%s параметрами не существует", courseId, publisherId)));
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for (var task : course.getTasks().stream().filter(task -> task.getType() == TaskType.CODE).toList()) {
            List<TaskProgress> taskProgresses = this.taskProgressRepository.findByTaskIdAndStatus(
                    task.getId(), ProgressStatus.REVIEW);
            if (!taskProgresses.isEmpty()) {
                taskProgresses.forEach(taskProgress -> {
                     CodeAnswer codeAnswer = this.codeAnswerRepository.findByTaskProgressId(taskProgress.getId()).orElse(null);
                     ReviewDto reviewDto = new ReviewDto();
                     reviewDto.setProgressId(taskProgress.id);
                     reviewDto.setTask(taskMapper.toDto(task));
                     reviewDto.setUserMail(taskProgress.getStudent().getEmail());
                     reviewDto.setCodeAnswer(CodeAnswerDto.builder()
                             .id(codeAnswer.id)
                             .taskProgressId(taskProgress.getId())
                             .repositoryLink(codeAnswer.getRepositoryLink())
                             .teacherComment(codeAnswer.getTeacherComment())
                             .build());
                    reviewDtos.add(reviewDto);
                });
            }
        }
        if (reviewDtos.isEmpty()) {
            return null;
        }
        return reviewDtos;
    }
}
