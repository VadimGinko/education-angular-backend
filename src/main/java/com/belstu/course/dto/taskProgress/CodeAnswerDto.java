package com.belstu.course.dto.taskProgress;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CodeAnswerDto {
    Long id;
    Long taskProgressId;
    String repositoryLink;
    String teacherComment;
}
