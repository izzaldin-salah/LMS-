package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "assignment_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "submission_file_path", nullable = false)
    private String submissionFilePath;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "assigned_grade")
    private Double assignedGrade;

    @Column(name = "instructor_feedback", length = 1000)
    private String instructorFeedback;
}
