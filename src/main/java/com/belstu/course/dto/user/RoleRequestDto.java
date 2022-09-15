package com.belstu.course.dto.user;

import javax.validation.constraints.NotBlank;

public record RoleRequestDto(
        @NotBlank(message = "Поле названия роли не может быть пустым")
        String name
) {
}
