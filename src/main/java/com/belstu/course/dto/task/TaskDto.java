package com.belstu.course.dto.task;

import com.belstu.course.model.enums.TaskType;
import lombok.Data;

import java.util.List;

@Data
public class TaskDto {
    Long id;
    String name;
    Integer order;
    Float rating;
    TaskType type;
    String content;
    Long courseId;
    List<TaskResourceDto> links;
}
