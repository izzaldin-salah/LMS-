package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentType assessmentType;

    @Column(nullable = false)
    private String assessmentTitle;

    @Column(length = 1000)
    private String assessmentDescription;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(length = 2000)
    private String instructions;

}
