package com.learnvault.learninganalyticsreporting.service;

import com.learnvault.learninganalyticsreporting.dto.request.LearningReportRequest;
import com.learnvault.learninganalyticsreporting.dto.response.LearningReportResponse;
import com.learnvault.learninganalyticsreporting.dto.response.ReportSummaryResponse;

import java.util.List;

public interface LearningReportService {
    LearningReportResponse generateReport(LearningReportRequest request);
    LearningReportResponse getReportById(Integer id);
    List<LearningReportResponse> getAllReports();
    ReportSummaryResponse getSummary();
}