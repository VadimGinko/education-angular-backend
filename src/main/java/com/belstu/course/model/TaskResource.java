package com.belstu.course.model;

import com.belstu.course.model.enums.ResourceType;
import lombok.*;

import javax.persistence.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class TaskResource {
    @Id
    @GeneratedValue
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String content;

    @Column(nullable = false)
    public ResourceType type;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    public Task task;
}
