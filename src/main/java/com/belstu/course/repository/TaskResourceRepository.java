package com.belstu.course.repository;

import com.belstu.course.model.TaskResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskResourceRepository extends JpaRepository<TaskResource, Long> {
}
