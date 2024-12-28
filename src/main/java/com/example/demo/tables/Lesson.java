package com.example.demo.tables;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "resource_file")
    private String resourceFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "attendance_otp")
    private String attendanceOtp;
}
