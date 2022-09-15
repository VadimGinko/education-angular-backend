package com.belstu.course.dto.courseType;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CourseTypeDto {
    Long id;
    @NotBlank(message = "Поле названия типа курса не может быть пустым")
    String name;
}
