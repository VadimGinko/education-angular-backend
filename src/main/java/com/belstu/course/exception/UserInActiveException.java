package com.belstu.course.exception;

public class UserInActiveException extends RuntimeException {
    public UserInActiveException(String message) {
        super(message);
    }
}
