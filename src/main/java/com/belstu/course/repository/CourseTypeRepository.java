package com.belstu.course.repository;

import com.belstu.course.model.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseTypeRepository extends JpaRepository<CourseType, Long> {
    Optional<CourseType> findByName(String name);
}
