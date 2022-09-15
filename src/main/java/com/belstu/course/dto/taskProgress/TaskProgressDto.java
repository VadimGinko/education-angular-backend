package com.belstu.course.dto.taskProgress;

import com.belstu.course.model.enums.ProgressStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskProgressDto {
    Long id;
    Long taskId;
    Long userId;
    ProgressStatus status;
}
