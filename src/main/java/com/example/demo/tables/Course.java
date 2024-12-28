package com.example.demo.tables;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be null")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description cannot be null")
    @Column(nullable = false)
    private String description;

    @NotBlank(message = "Duration cannot be null")
    @Column(nullable = false)
    private String duration;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    @JsonIgnore // Exclude the entire instructor object from the response
    private User instructor;

    @JsonGetter("instructorName")
    public String getInstructorName() {
        return instructor != null ? instructor.getFullName() : null;
    }

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Lesson> lessons;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CourseRegistration> enrollments;
}
