package com.example.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tables.Attendance;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}