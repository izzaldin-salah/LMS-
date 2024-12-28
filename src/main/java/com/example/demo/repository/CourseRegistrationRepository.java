package com.example.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.tables.Course;
import com.example.demo.tables.CourseRegistration;
import com.example.demo.tables.User;

import java.util.Optional;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {

     /**
     * Finds a CourseRegistration by a student and course.
     *
     * @param student the user enrolled in the course
     * @param course  the course
     * @return an optional CourseRegistration
     */
    Optional<CourseRegistration> findByStudentAndCourse(User student, Course course);
}

