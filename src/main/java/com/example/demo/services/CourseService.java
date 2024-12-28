package com.example.demo.services;

import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.CourseRegistrationRepository;
import com.example.demo.repository.LessonRepository;
import com.example.demo.tables.Attendance;
import com.example.demo.tables.Course;
import com.example.demo.tables.CourseRegistration;
import com.example.demo.tables.Lesson;
import com.example.demo.tables.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final LessonRepository lessonRepository;
    private final OtpService otpService;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    private NotificationService notificationService;


    public Course createCourse(String name, String description, String duration, User instructor) {
        Course course = Course.builder()
                .name(name)
                .description(description)
                .duration(duration)
                .instructor(instructor)
                .build();
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }


    public void enrollStudent(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Optional<CourseRegistration> existingEnrollment = courseRegistrationRepository.findByStudentAndCourse(student, course);

        if (existingEnrollment.isEmpty()) {
            CourseRegistration enrollment = CourseRegistration.builder()
                    .course(course)
                    .student(student)
                    .build();
                    courseRegistrationRepository.save(enrollment);

            // Notify the student and instructor
            notificationService.sendNotification(student.getId(), "You have successfully enrolled in the course!");
            notificationService.sendNotification(course.getInstructor().getId(), "A new student has enrolled in your course!");
        } else {
            throw new RuntimeException("Student is already enrolled in the course.");
        }
    }

    public List<User> getEnrolledStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getEnrollments().stream()
                .map(CourseRegistration::getStudent)
                .collect(Collectors.toList());
    }


    public Lesson addLessonToCourse(Long courseId, String title, String videoUrl, String resourceFile) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Lesson lesson = Lesson.builder()
                .title(title)
                .videoUrl(videoUrl)
                .resourceFile(resourceFile)
                .course(course)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);


        course.getEnrollments().forEach(enrollment ->
                notificationService.sendNotification(enrollment.getStudent().getId(), "A new lesson has been added: " + title));

        return savedLesson;
    }


    public String generateOtpForLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        String otp = otpService.generateOtp();
        lesson.setAttendanceOtp(otp);
        lessonRepository.save(lesson);
        return otp;
    }


    public boolean markAttendance(Long lessonId, User student, String otp) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!otp.equals(lesson.getAttendanceOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }

        Attendance attendance = Attendance.builder()
                .otp(otp)
                .student(student)
                .lesson(lesson)
                .attended(true)
                .build();

        attendanceRepository.save(attendance); // Save attendance in the database

        return true;
    }

    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}
