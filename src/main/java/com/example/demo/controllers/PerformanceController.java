package com.example.demo.controllers;

import com.example.demo.services.PerformanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/course/{courseId}/chart")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR','ROLE_ADMIN')")
    public Map<String, Double> getCourseChart(@PathVariable Long courseId) {
        return performanceService.getCourseQuizPerformance(courseId);
    }

    @GetMapping("/course/{courseId}/report")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR','ROLE_ADMIN')")
    public byte[] getExcelReport(@PathVariable Long courseId) throws Exception {
        return performanceService.generateExcelReport(courseId);
    }
}
