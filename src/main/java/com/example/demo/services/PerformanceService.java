package com.example.demo.services;

import com.example.demo.repository.CourseRegistrationRepository;
import com.example.demo.repository.QuizAttemptRepository;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.tables.CourseRegistration;
import com.example.demo.tables.QuizAttempt;
import com.example.demo.tables.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Apache POI imports
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Java IO import
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubmissionRepository submissionRepository;


    public Map<String, Double> getCourseQuizPerformance(Long courseId) {
        List<CourseRegistration> registrations = courseRegistrationRepository.findAll();
        return registrations.stream()
                .filter(reg -> reg.getCourse().getId().equals(courseId))
                .collect(Collectors.toMap(
                        reg -> reg.getStudent().getFullName(),
                        reg -> quizAttemptRepository.findAll().stream()
                                .filter(attempt -> attempt.getStudent().getId().equals(reg.getStudent().getId()))
                                .mapToDouble(QuizAttempt::getScore)
                                .average()
                                .orElse(0.0)
                ));
    }


    public byte[] generateExcelReport(Long courseId) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Performance");

        // Create header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student");
        header.createCell(1).setCellValue("Avg Quiz Score");
        header.createCell(2).setCellValue("Avg Assignment Grade");

        List<CourseRegistration> registrations = courseRegistrationRepository.findAll();
        List<CourseRegistration> filteredRegistrations = registrations.stream()
                .filter(reg -> reg.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());

        int rowIdx = 1;
        for (CourseRegistration reg : filteredRegistrations) {
            User student = reg.getStudent();

            // Average quiz score
            double avgQuizScore = quizAttemptRepository.findAll().stream()
                    .filter(attempt -> attempt.getStudent().getId().equals(student.getId()))
                    .mapToDouble(QuizAttempt::getScore)
                    .average()
                    .orElse(0.0);

            // Average assignment grade
            double avgAssignmentGrade = submissionRepository.findAll().stream()
                    .filter(submission -> submission.getStudent().getId().equals(student.getId()))
                    .mapToDouble(sub -> sub.getAssignedGrade() == null ? 0.0 : sub.getAssignedGrade())
                    .average()
                    .orElse(0.0);

            // Fill row with data
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(student.getFullName());
            row.createCell(1).setCellValue(avgQuizScore);
            row.createCell(2).setCellValue(avgAssignmentGrade);
        }

        // Write workbook to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
