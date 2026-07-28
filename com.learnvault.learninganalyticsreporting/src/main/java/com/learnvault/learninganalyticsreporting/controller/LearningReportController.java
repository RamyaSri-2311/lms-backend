package com.learnvault.learninganalyticsreporting.controller;

import com.learnvault.learninganalyticsreporting.dto.request.LearningReportRequest;
import com.learnvault.learninganalyticsreporting.dto.response.LearningReportResponse;
import com.learnvault.learninganalyticsreporting.dto.response.ReportSummaryResponse;
import com.learnvault.learninganalyticsreporting.service.LearningReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-reports")
@RequiredArgsConstructor
public class LearningReportController {

    private final LearningReportService learningReportService;

    @PostMapping
    public ResponseEntity<LearningReportResponse> generateReport(@RequestBody LearningReportRequest request) {
        return new ResponseEntity<>(learningReportService.generateReport(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LearningReportResponse>> getAllReports() {
        return ResponseEntity.ok(learningReportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningReportResponse> getReportById(@PathVariable Integer id) {
        return ResponseEntity.ok(learningReportService.getReportById(id));
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getSummary() {
        return ResponseEntity.ok(learningReportService.getSummary());
    }
    
    
}