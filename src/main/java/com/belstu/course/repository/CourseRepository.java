package com.belstu.course.repository;

import com.belstu.course.model.Course;
import com.belstu.course.model.CourseType;
import com.belstu.course.model.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Boolean existsByName(String name);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByPublisherId(Long publisherId);

    List<Course> findByCreatedOnBefore(LocalDate createdOn);
    List<Course> findAllByType(CourseType type);
    List<Course> findAllByIdNotIn(Collection<Long> id);




    Optional<Course> findByIdAndPublisherId(Long id, Long publisherId);

    void deleteById(Long courseId);

    List<Course> findAll();
}
