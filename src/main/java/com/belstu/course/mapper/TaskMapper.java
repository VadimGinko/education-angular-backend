package com.belstu.course.mapper;

import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task source);

    List<TaskDto> toDto(List<Task> source);
}
