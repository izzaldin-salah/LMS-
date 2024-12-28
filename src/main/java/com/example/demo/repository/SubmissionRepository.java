package com.example.demo.repository;

import com.example.demo.tables.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssessment_AssessmentId(Long assessmentId);
}
