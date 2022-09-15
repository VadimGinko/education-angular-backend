package com.belstu.course.repository;

import com.belstu.course.model.TaskProgress;
import com.belstu.course.model.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long> {
    Optional<TaskProgress> findByStudentIdAndTaskId(Long userId, Long taskId);

    List<TaskProgress> findByTaskIdAndStatus(Long taskId, ProgressStatus status);
}
