package com.belstu.course.exception.advice;

import com.belstu.course.dto.ErrorDto;
import com.belstu.course.dto.ValidationErrorDto;
import com.belstu.course.exception.JwtAuthenticationException;
import com.belstu.course.exception.RegistrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    public ErrorDto handleAccessDeniedExceptions(Exception ex) {
        ErrorDto error = new ErrorDto("Access is denied");
        log.error(String.valueOf(error));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ValidationErrorDto handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(objectError -> new ValidationErrorDto(
                        ((FieldError) objectError).getField(),
                        objectError.getDefaultMessage()
                ))
                .orElseGet(() -> new ValidationErrorDto("", ""));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            RegistrationException.class,
            UsernameNotFoundException.class,
            JwtAuthenticationException.class,
            Exception.class})
    @ResponseBody
    public ErrorDto handleExceptions(Exception ex) {
        ErrorDto error = new ErrorDto(ex.getMessage());
        log.error(String.valueOf(error));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({SQLException.class})
    @ResponseBody
    public ErrorDto handleSqlExceptions(Exception ex) {
        ErrorDto error = new ErrorDto("duplicate");
        log.error(String.valueOf(error));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadCredentialsException.class})
    @ResponseBody
    public ErrorDto handleBadCredentialsException() {
        log.error("Неверный логин или пароль");
        return new ErrorDto("Неверный логин или пароль");
    }
}
