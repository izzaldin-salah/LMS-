package com.example.demo.services;

import com.example.demo.repository.*;
import com.example.demo.tables.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubmissionRepository submissionRepository;
    @Autowired
    private NotificationService notificationService;

    public Assessment createAssessment(Long courseId, String title, String type, String instructions, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // if (!course.getInstructor().getId().equals(instructor.getId()) && instructor.getType() != Usertype.ADMIN) {
        //     throw new RuntimeException("Not Authorized");
        // }

        AssessmentType assessmentType = AssessmentType.valueOf(type.toUpperCase());
        Assessment assessment = Assessment.builder()
                .assessmentTitle(title)
                .assessmentType(assessmentType)
                .course(course)
                .instructions(instructions)
                .build();

        return assessmentRepository.save(assessment);
    }

    public Question addQuestionToQuiz(Long assessmentId, String questionText, String questionType, String options, String correctAnswer, User instructor ) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (assessment.getAssessmentType() != AssessmentType.QUIZ) {
            throw new RuntimeException("Not a quiz");
        }


        Question question = Question.builder()
                .assessment(assessment)
                .questionText(questionText)
                .questionType(questionType)
                .options(options)
                .correctAnswer(correctAnswer)
                .build();

        return questionRepository.save(question);
    }

    public double submitQuiz(Long quizId, Map<Long, String> answers, User student) {
        Assessment quiz = assessmentRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getAssessmentType() != AssessmentType.QUIZ) {
            throw new RuntimeException("Not a quiz");
        }

        List<Question> questions = questionRepository.findByAssessment_AssessmentId(quiz.getAssessmentId());
        double score = 0.0;

        for (Question question : questions) {
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null && studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                score += 1.0;
            }
        }

        double finalScore = (questions.size() > 0) ? (score / questions.size()) * 100.0 : 0.0;

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .score(finalScore)
                .build();

        quizAttemptRepository.save(attempt);
        return finalScore;
    }

    public AssignmentSubmission submitAssignment(Long assignmentId, User student, String filePath) {
        Assessment assignment = assessmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    
        if (assignment.getAssessmentType() != AssessmentType.ASSIGNMENT) {
            throw new RuntimeException("Not an assignment");
        }
    
        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assessment(assignment) // Use the correct field name here
                .student(student)
                .submissionFilePath(filePath)
                .submissionDate(LocalDateTime.now())
                .build();
    
        return submissionRepository.save(submission);
    }
    public AssignmentSubmission gradeAssignment(Long submissionId, Long studentId, Double grade, String feedback, User instructor) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    
        // // Verify instructor authorization
        // if (!submission.getAssessment().getCourse().getInstructor().getId().equals(instructor.getId())
        //         && instructor.getType() != Usertype.ADMIN) {
        //     throw new RuntimeException("Not Authorized");
        // }
    
        submission.setAssignedGrade(grade);
        submission.setInstructorFeedback(feedback);
        notificationService.sendNotification(studentId, "Your assignment has been graded. Grade: " + grade);
    
        return submissionRepository.save(submission);
    }
    
    
}
