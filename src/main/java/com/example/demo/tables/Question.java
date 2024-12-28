package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "question_type", nullable = false)
    private String questionType;

    @Column(length = 2000)
    private String options;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;
}
