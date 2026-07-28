package com.learnvault.learninganalyticsreporting.service.impl;

import com.learnvault.learninganalyticsreporting.client.AssessmentEvaluationClient;
import com.learnvault.learninganalyticsreporting.client.EnrollmentLearningProgressClient;
import com.learnvault.learninganalyticsreporting.client.IdentityAccessManagementClient;
import com.learnvault.learninganalyticsreporting.dto.request.LearningReportRequest;
import com.learnvault.learninganalyticsreporting.dto.response.LearningReportResponse;
import com.learnvault.learninganalyticsreporting.dto.response.ReportSummaryResponse;
import com.learnvault.learninganalyticsreporting.entity.LearningReport;
import com.learnvault.learninganalyticsreporting.exception.ResourceNotFoundException;
import com.learnvault.learninganalyticsreporting.repository.LearningReportRepository;
import com.learnvault.learninganalyticsreporting.service.LearningReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningReportServiceImpl implements LearningReportService {

    private final LearningReportRepository learningReportRepository;
    private final EnrollmentLearningProgressClient enrollmentClient;
    private final AssessmentEvaluationClient assessmentClient;
    private final IdentityAccessManagementClient identityClient;

    @Override
    public LearningReportResponse generateReport(LearningReportRequest request) {
        log.info("Generating report for scope: {}", request.getScope());

        String metrics = buildMetricsByScope(request.getScope());

        LearningReport report = LearningReport.builder()
                .scope(request.getScope())
                .metrics(metrics)
                .build();

        LearningReport saved = learningReportRepository.save(report);
        return mapToResponse(saved);
    }

    @Override
    public LearningReportResponse getReportById(Integer id) {
        log.info("Fetching report by ID: {}", id);
        LearningReport report = learningReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
        return mapToResponse(report);
    }

    @Override
    public List<LearningReportResponse> getAllReports() {
        log.info("Fetching all reports");
        return learningReportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReportSummaryResponse getSummary() {
        log.info("Generating summary dashboard");

        int totalUsers = 0;
        int totalEnrollments = 0;
        int totalAssessments = 0;
        int totalAttempts = 0;
        double avgScore = 0.0;

        try {
            totalUsers = identityClient.getAllUsers().size();
        } catch (Exception e) {
            log.warn("Could not fetch users: {}", e.getMessage());
        }

        try {
            totalEnrollments = enrollmentClient.getAllEnrollments().size();
        } catch (Exception e) {
            log.warn("Could not fetch enrollments: {}", e.getMessage());
        }

        try {
            totalAssessments = assessmentClient.getAllAssessments().size();
        } catch (Exception e) {
            log.warn("Could not fetch assessments: {}", e.getMessage());
        }

        try {
            List<Map<String, Object>> attempts = assessmentClient.getAllAttempts();
            totalAttempts = attempts.size();
            double totalScore = attempts.stream()
                    .mapToInt(a -> (Integer) a.getOrDefault("score", 0))
                    .sum();
            avgScore = totalAttempts > 0 ? totalScore / totalAttempts : 0.0;
        } catch (Exception e) {
            log.warn("Could not fetch attempts: {}", e.getMessage());
        }

        double completionRate = totalEnrollments > 0
                ? (double) totalAttempts / totalEnrollments * 100
                : 0.0;

        return ReportSummaryResponse.builder()
                .totalUsers(totalUsers)
                .totalEnrollments(totalEnrollments)
                .totalAssessments(totalAssessments)
                .totalAttempts(totalAttempts)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .averageScore(Math.round(avgScore * 100.0) / 100.0)
                .build();
    }

    private String buildMetricsByScope(String scope) {
        try {
            switch (scope.toUpperCase()) {
                case "ENROLLMENT":
                    int count = enrollmentClient.getAllEnrollments().size();
                    return String.format("{\"totalEnrollments\": %d}", count);
                case "ASSESSMENT":
                    int aCount = assessmentClient.getAllAssessments().size();
                    return String.format("{\"totalAssessments\": %d}", aCount);
                case "USER":
                    int uCount = identityClient.getAllUsers().size();
                    return String.format("{\"totalUsers\": %d}", uCount);
                default:
                    return "{\"message\": \"Custom report scope\"}";
            }
        } catch (Exception e) {
            log.error("Error building metrics: {}", e.getMessage());
            return "{\"error\": \"Failed to fetch metrics\"}";
        }
    }

    private LearningReportResponse mapToResponse(LearningReport report) {
        return LearningReportResponse.builder()
                .reportId(report.getReportId())
                .scope(report.getScope())
                .metrics(report.getMetrics())
                .generatedDate(report.getGeneratedDate())
                .build();
    }
}