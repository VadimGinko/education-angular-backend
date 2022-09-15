package com.belstu.course.mapper;

import com.belstu.course.dto.task.TaskResourceDto;
import com.belstu.course.model.TaskResource;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskLinkMapper {
    TaskResourceDto toDto(TaskResource source);

    List<TaskResourceDto> toDto(List<TaskResource> source);
}
