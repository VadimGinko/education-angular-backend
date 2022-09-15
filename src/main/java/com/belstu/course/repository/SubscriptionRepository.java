package com.belstu.course.repository;

import com.belstu.course.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByCourseId(Long courseId);

    Optional<Subscription> findByUserIdAndCourseId(Long userId, Long courseId);
}
