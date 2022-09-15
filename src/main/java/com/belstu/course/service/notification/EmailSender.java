package com.belstu.course.service.notification;


public interface EmailSender {
    void send(String to, String email);
}
