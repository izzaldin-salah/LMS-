package com.example.demo.controllers;

import com.example.demo.services.CourseService;
import com.example.demo.services.JwtUtil;
import com.example.demo.services.Userservice;
import com.example.demo.tables.Course;
import com.example.demo.tables.Lesson;
import com.example.demo.tables.User;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final Userservice userService;
    private final JwtUtil jwtUtil;

    public CourseController(CourseService courseService, Userservice userService, JwtUtil jwtUtil) {
        this.courseService = courseService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Course createCourse(@RequestBody Map<String, String> requestPayload, HttpServletRequest request) {
        String name = requestPayload.get("name");
        String description = requestPayload.get("description");
        String duration = requestPayload.get("duration");

        Long userId = jwtUtil.extractUserId(request);
        User instructor = userService.findById(userId);
        return courseService.createCourse(name, description, duration, instructor);
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/{courseId}/enroll") //STUDENT
    public void enrollStudent(@PathVariable Long courseId, HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        User student = userService.findById(userId);
        courseService.enrollStudent(courseId, student);
    }

    @GetMapping("/{courseId}/students") //ADMIN INSRUCTOR
    public List<User> getEnrolledStudents(@PathVariable Long courseId) {
        return courseService.getEnrolledStudents(courseId);
    }

    @PostMapping("/{courseId}/lessons")
    public Lesson addLesson(@PathVariable Long courseId, @RequestBody Map<String, String> requestPayload) {
        String title = requestPayload.get("title");
        String videoUrl = requestPayload.get("videoUrl");
        String resourceFile = requestPayload.get("resourceFile");

        return courseService.addLessonToCourse(courseId, title, videoUrl, resourceFile);
    }

    @PostMapping("/lessons/{lessonId}/generate-otp") //INSTRUCTOR
    public String generateOtp(@PathVariable Long lessonId) {
        return courseService.generateOtpForLesson(lessonId);
    }

    @PostMapping("/lessons/{lessonId}/attend") //STUDENT
    public boolean attendLesson(@PathVariable Long lessonId, @RequestBody Map<String, String> requestPayload, HttpServletRequest request) {
        String otp = requestPayload.get("otp");

        Long userId = jwtUtil.extractUserId(request);
        User student = userService.findById(userId);
        return courseService.markAttendance(lessonId, student, otp);
    }
}
