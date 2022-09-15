package com.belstu.course.mapper;

import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.model.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto toDto(Course source);

    List<CourseDto> toDto(List<Course> source);
}
