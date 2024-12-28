package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a Quiz Attempt entity in the database.
 */
@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    /**
     * Unique identifier for the quiz attempt.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student who made the quiz attempt.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /**
     * The quiz associated with this attempt (must be of type QUIZ).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment quiz;

    /**
     * The score achieved in this quiz attempt.
     */
    @Column(nullable = false)
    private double score;
}
