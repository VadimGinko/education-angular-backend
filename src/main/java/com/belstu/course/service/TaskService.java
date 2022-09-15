package com.belstu.course.service;

import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.mapper.CourseMapper;
import com.belstu.course.model.Task;
import com.belstu.course.repository.CourseRepository;
import com.belstu.course.repository.TaskRepository;
import com.dropbox.core.v2.DbxClientV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final CourseMapper courseMapper;
    private final DbxClientV2 dropboxClient;

    public Task create(TaskDto taskDto) throws Exception {
        var course = courseRepository.findById(taskDto.getCourseId()).orElseThrow(
                () -> new Exception(String.format("Курса с id=%s не существует", taskDto.getCourseId())));
        Task task = taskRepository.save(
                Task.builder()
                        .name(taskDto.getName())
                        .course(course)
                        .content(taskDto.getContent())
                        .type(taskDto.getType())
                        .order(course.getTasks().size() + 1)
                        .build()
        );
        dropboxClient.files().createFolderV2("/" + task.getId().toString());
        return task;
    }

    public Task edit(Long taskId, TaskDto taskDto) throws Exception {
        var task = taskRepository.findById(taskId).orElseThrow(
                () -> new Exception(String.format("Задачи с id=%s не существует", taskId)));
        task.setName(taskDto.getName());
        task.setContent(taskDto.getContent());
        return taskRepository.save(task);
    }

    public void editTasksOrder(List<TaskDto> tasks) {
        tasks.forEach(i -> {
            var task = this.taskRepository.getById(i.getId());
            task.setOrder(i.getOrder());
            this.taskRepository.save(task);
        });
    }

    public Task get(Long taskId) {
        return taskRepository.getById(taskId);
    }

}
