package com.belstu.course.repository;

import com.belstu.course.model.CodeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeAnswerRepository extends JpaRepository<CodeAnswer, Long> {
    Optional<CodeAnswer> findByTaskProgressId(Long taskProgressId);
}
