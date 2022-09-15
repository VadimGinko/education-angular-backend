package com.belstu.course.model;

import lombok.*;

import javax.persistence.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class CodeAnswer {
    @Id
    @GeneratedValue
    public Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = false)
    public TaskProgress taskProgress;

    @Column(nullable = false)
    public String repositoryLink;

    @Column(length = 10000)
    public String teacherComment;
}
