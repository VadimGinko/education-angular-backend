package com.belstu.course.dto;

import com.belstu.course.dto.user.RoleRequestDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record RegistrationRequestDto(
        @NotBlank(message = "Поле почты не может быть пустым")
        @Email(message = "Неверный формат почты")
        String email,

        @NotBlank(message = "Поле имени не может быть пустым")
        String firstName,

        @NotBlank(message = "Поле фамилии не может быть пустым")
        String lastName,

        RoleRequestDto role,

        @NotBlank(message = "Поле пароля не может быть пустым")
        @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",
                message = "Пароль должен содержать только латинские буквы и цифры и быть в промежутке от 8 до 20 симоволов")
        String password
) {
}
