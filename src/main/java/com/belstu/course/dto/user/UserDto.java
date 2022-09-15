package com.belstu.course.dto.user;

import com.belstu.course.model.Role;
import com.belstu.course.model.enums.UserStatus;
import lombok.Data;

@Data
public class UserDto {
    Long id;
    String email;
    String firstName;
    String lastName;
    String description;
    String link;
    UserStatus status;
    Role role;
}
