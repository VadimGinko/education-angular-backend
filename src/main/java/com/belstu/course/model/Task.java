package com.belstu.course.model;

import com.belstu.course.model.enums.TaskType;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Task implements Cloneable {
    @Id
    @GeneratedValue
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(name = "\"order\"", nullable = false)
    public Integer order;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public TaskType type;

    @Column(nullable = false, length = 100000)
    public String content;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    public Course course;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    public Collection<TaskResource> links;

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
