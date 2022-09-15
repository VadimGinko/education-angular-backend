package com.belstu.course.dto;

public record ValidationErrorDto(
        String scope,
        String message
) {
}
