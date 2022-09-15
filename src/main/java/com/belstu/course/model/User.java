package com.belstu.course.model;

import com.belstu.course.model.enums.UserStatus;
import lombok.*;

import javax.persistence.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue
    public Long id;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String firstName;

    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false)
    public String password;

    @Column
    public String description;

    @Column
    public String link;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public UserStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Role role;
}
