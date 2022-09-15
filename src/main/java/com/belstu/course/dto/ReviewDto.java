package com.belstu.course.dto;

import com.belstu.course.dto.task.TaskDto;
import com.belstu.course.dto.taskProgress.CodeAnswerDto;
import lombok.Data;

@Data
public class ReviewDto {
    private Long progressId;
    private String userMail;
    private TaskDto task;
    private CodeAnswerDto codeAnswer;
}
