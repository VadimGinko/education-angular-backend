package com.belstu.course.model;

import com.belstu.course.model.enums.ProgressStatus;
import lombok.*;

import javax.persistence.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class TaskProgress {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Task task;

    @ManyToOne
    @JoinColumn(nullable = false)
    public User student;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public ProgressStatus status;
}
