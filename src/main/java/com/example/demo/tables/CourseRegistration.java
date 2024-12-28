package com.example.demo.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "course_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    private Course course;
}
