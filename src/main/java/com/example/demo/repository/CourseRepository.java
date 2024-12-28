package com.example.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tables.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {



}

