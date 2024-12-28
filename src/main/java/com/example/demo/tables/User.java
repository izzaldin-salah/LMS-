package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity representing a user in the system.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Usertype type; // ADMIN, INSTRUCTOR, STUDENT

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    /**
     * A user (e.g., a student) can have multiple enrollments in courses.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CourseRegistration> enrollments;
}
