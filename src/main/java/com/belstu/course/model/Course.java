package com.belstu.course.model;

import com.belstu.course.model.enums.CourseStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Course {
    @Id
    @GeneratedValue
    public Long id;

    @CreatedDate
    public LocalDate createdOn;

    @Column(unique = true, nullable = false)
    public String name;

    @Column(nullable = false, length = 100000)
    public String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    public CourseType type;

    @ManyToOne
    @JoinColumn(nullable = false)
    public User publisher;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public Collection<Task> tasks;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public CourseStatus status;
}
