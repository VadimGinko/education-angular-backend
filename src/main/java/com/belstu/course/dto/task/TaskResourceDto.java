package com.belstu.course.dto.task;

import com.belstu.course.model.enums.ResourceType;
import lombok.Data;

@Data
public class TaskResourceDto {
    Long id;
    String name;
    String content;
    ResourceType type;
}
