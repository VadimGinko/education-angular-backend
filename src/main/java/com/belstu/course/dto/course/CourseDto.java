package com.belstu.course.dto.course;

import com.belstu.course.dto.courseType.CourseTypeDto;
import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.dto.user.UserDto;
import com.belstu.course.model.enums.CourseStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CourseDto {
    Long id;
    String name;
    String description;
    Double rating;
    CourseStatus status;
    CourseTypeDto type;
    UserDto publisher;
    LocalDate createdOn;
    List<TaskDto> tasks;
}
