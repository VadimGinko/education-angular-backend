package com.belstu.course.mapper;

import com.belstu.course.dto.user.UserDto;
import com.belstu.course.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User source);

    List<UserDto> toDto(List<User> source);
}
