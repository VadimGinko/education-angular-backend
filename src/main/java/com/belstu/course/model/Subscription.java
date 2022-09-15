package com.belstu.course.model;

import com.belstu.course.model.enums.ActivityStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Subscription {
    @Id
    @GeneratedValue
    public Long id;

    @CreatedDate
    public LocalDate createdOn;

    @ManyToOne
    @JoinColumn(nullable = false)
    public User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Course course;

    @Column(nullable = false)
    public Integer rating;

    public static void main(String[] args) {
        var q = 4860 + 486 + 1849.72 + 62.27 + 81.22 + 486 + 5346;
        System.out.printf(String.valueOf(q));
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ActivityStatus status;
}
