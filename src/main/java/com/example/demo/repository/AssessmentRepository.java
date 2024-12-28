package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tables.Assessment;


public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}

