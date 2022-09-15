package com.belstu.course.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record LoginRequestDto(
        @NotBlank(message = "Поле почты не может быть пустым")
        @Email(message = "Неверный формат почты")
        String email,

        @NotBlank(message = "Поле пароля не может быть пустым")
        @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",
                message = "Неверный формат пароля")
        String password
) {
}
