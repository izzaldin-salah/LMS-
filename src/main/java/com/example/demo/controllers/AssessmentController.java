package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.tables.Assessment;
import com.example.demo.tables.AssignmentSubmission;
import com.example.demo.tables.Question;
import com.example.demo.tables.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final Userservice userService;
    private final JwtUtil jwtUtil;

    public AssessmentController(AssessmentService assessmentService,
                                Userservice userService,
                                JwtUtil jwtUtil) {
        this.assessmentService = assessmentService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Assessment createAssessment(@RequestBody Map<String, String> requestPayload, HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        User instructor = userService.findById(userId);

        Long courseId = Long.parseLong(requestPayload.get("courseId"));
        String title = requestPayload.get("title");
        String type = requestPayload.get("type");
        String instructions = requestPayload.get("instructions");

        return assessmentService.createAssessment(courseId, title, type, instructions, instructor);
    }

@PostMapping("/{assessmentId}/questions")
// @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
public ResponseEntity<Map<String, Object>> addQuestionToQuiz(
        @PathVariable Long assessmentId,
        @RequestBody Map<String, String> requestPayload,
        HttpServletRequest request) {

    Long userId = jwtUtil.extractUserId(request);
    User instructor = userService.findById(userId); // Ensure the user is authorized to modify the assessment

    String questionText = requestPayload.get("questionText");
    String questionType = requestPayload.get("questionType");
    String options = requestPayload.get("options");
    String correctAnswer = requestPayload.get("correctAnswer");

    Question question = assessmentService.addQuestionToQuiz(
            assessmentId, questionText, questionType, options, correctAnswer, instructor);

    // Construct response message
    Map<String, Object> response = Map.of(
            "message", "Question added successfully!",
            "question", question
    );

    return ResponseEntity.ok(response);
}



    @PostMapping("/{quizId}/submit-quiz")
    @PreAuthorize("hasRole('STUDENT')")
    public double submitQuiz(@PathVariable Long quizId, @RequestBody Map<Long, String> answers, HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        User student = userService.findById(userId);
        return assessmentService.submitQuiz(quizId, answers, student);
    }

    @PostMapping("/{assignmentId}/submit-assignment")
    // @PreAuthorize("hasRole('STUDENT')")
    public AssignmentSubmission submitAssignment(@PathVariable Long assignmentId,
                                                 @RequestBody Map<String, String> requestPayload,
                                                 HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        User student = userService.findById(userId);
        String filePath = requestPayload.get("filePath");

        return assessmentService.submitAssignment(assignmentId, student, filePath);
    }

    @PostMapping("/{submissionId}/grade-assignment")
    // @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public AssignmentSubmission gradeAssignment(@PathVariable Long submissionId,
                                                @RequestBody Map<String, Object> requestPayload,
                                                HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        User instructor = userService.findById(userId);

        Long studentId = Long.parseLong(requestPayload.get("studentId").toString());
        Double grade = Double.parseDouble(requestPayload.get("grade").toString());
        String feedback = requestPayload.get("feedback").toString();

        return assessmentService.gradeAssignment(submissionId, studentId, grade, feedback, instructor);
    }
}
