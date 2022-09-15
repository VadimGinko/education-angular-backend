package com.belstu.course.dto.subscription;

import com.belstu.course.dto.course.CourseDto;
import com.belstu.course.dto.user.UserDto;
import com.belstu.course.model.enums.ActivityStatus;
import lombok.Data;

@Data
public class SubscriptionDto {
    Long id;
    CourseDto course;
    Integer rating;
    UserDto user;
    ActivityStatus status;
}
