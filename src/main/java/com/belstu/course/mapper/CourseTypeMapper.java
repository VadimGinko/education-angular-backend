package com.belstu.course.mapper;

import com.belstu.course.dto.courseType.CourseTypeDto;
import com.belstu.course.model.CourseType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseTypeMapper {
    CourseTypeDto toDto(CourseType source);

    List<CourseTypeDto> toDto(List<CourseType> source);
}
