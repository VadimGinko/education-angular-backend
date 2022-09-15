package com.belstu.course.service;

import com.belstu.course.dto.task.TaskResourceDto;
import com.belstu.course.mapper.TaskLinkMapper;
import com.belstu.course.model.TaskResource;
import com.belstu.course.repository.TaskRepository;
import com.belstu.course.repository.TaskResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskResourceService {
    private final TaskLinkMapper taskLinkMapper;
    private final TaskResourceRepository taskResourceRepository;
    private final TaskRepository taskRepository;

    public void create(Long taskId, TaskResourceDto taskResourceDto) throws Exception {
        var task = taskRepository.findById(taskId).orElseThrow(
                () -> new Exception(String.format("Задачи с id=%s не существует", taskId)));
        taskResourceRepository.save(
                TaskResource.builder()
                        .name(taskResourceDto.getName())
                        .content(taskResourceDto.getContent())
                        .type(taskResourceDto.getType())
                        .task(task)
                        .build()
        );
    }

    public TaskResource getById(Long id) {
        return taskResourceRepository.getById(id);
    }

    public void remove(Long id) {
        taskResourceRepository.deleteById(id);
    }
}
