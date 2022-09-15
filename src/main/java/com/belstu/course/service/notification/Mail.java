package com.belstu.course.service.notification;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Mail {
    private String from;
    private String to;
    private String subject;
    private String mailContent;
}