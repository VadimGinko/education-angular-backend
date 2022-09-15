package com.belstu.course.dto.user;

import com.belstu.course.model.enums.UserStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ChangeUserStatusDto(
        @NotBlank(message = "Поле почты не может быть пустым")
        String email,
        @NotNull(message = "Поле статуса не может быть пустым")
        UserStatus status
) {
}
