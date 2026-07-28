package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.dto.request.LearningReportRequest;
import com.learnvault.identityaccessmanagement.dto.response.LearningReportResponse;
import com.learnvault.identityaccessmanagement.entity.LearningReport;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.LearningReportRepository;
import com.learnvault.identityaccessmanagement.service.LearningReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningReportServiceImpl implements LearningReportService {

    private final LearningReportRepository learningReportRepository;

    @Override
    public LearningReportResponse generateReport(LearningReportRequest request) {
        log.info("Generating report for scope: {}", request.getScope());

        String metrics = buildMetricsJson(request);

        LearningReport report = LearningReport.builder()
                .scope(request.getScope())
                .metrics(metrics)
                .build();

        LearningReport saved = learningReportRepository.save(report);
        return mapToResponse(saved);
    }

    @Override
    public LearningReportResponse getReportById(Integer id) {
        LearningReport report = learningReportRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found with id: " + id));

        return mapToResponse(report);
    }

    @Override
    public List<LearningReportResponse> getAllReports() {
        return learningReportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LearningReportResponse getSummary() {

        String summaryMetrics =
                "{\"totalEnrollments\":120," +
                "\"completionRate\":85," +
                "\"certifications\":340," +
                "\"avgScore\":92}";

        LearningReport report = LearningReport.builder()
                .scope("SUMMARY")
                .metrics(summaryMetrics)
                .build();

        LearningReport saved = learningReportRepository.save(report);

        return mapToResponse(saved);
    }

    /**
     * Builds a simple JSON string containing report details.
     */
    private String buildMetricsJson(LearningReportRequest request) {

        return String.format(
                "{\"scope\":\"%s\",\"generatedAt\":\"%s\"}",
                request.getScope(),
                LocalDateTime.now()
        );
    }

    /**
     * Converts Entity to Response DTO.
     */
    private LearningReportResponse mapToResponse(LearningReport report) {

        return LearningReportResponse.builder()
                .reportId(report.getReportId())
                .scope(report.getScope())
                .metrics(report.getMetrics())
                .generatedDate(report.getGeneratedDate())
                .build();
    }
}