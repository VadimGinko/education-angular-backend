package com.belstu.course.dto;

import com.belstu.course.dto.user.RoleRequestDto;

public record LoginResponseDto(
        Long id,
        String email,
        RoleRequestDto role,
        String firstName,
        String lastName,
        String description,
        String link
) {
}
