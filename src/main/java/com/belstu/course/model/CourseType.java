package com.belstu.course.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class CourseType {
    @Id
    @GeneratedValue
    public Long id;

    @Column(unique = true, nullable = false)
    public String name;
}
