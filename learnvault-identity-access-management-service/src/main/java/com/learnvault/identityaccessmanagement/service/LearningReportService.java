package com.learnvault.identityaccessmanagement.service;

import com.learnvault.identityaccessmanagement.dto.request.LearningReportRequest;
import com.learnvault.identityaccessmanagement.dto.response.LearningReportResponse;

import java.util.List;

public interface LearningReportService {
    LearningReportResponse generateReport(LearningReportRequest request);
    LearningReportResponse getReportById(Integer id);
    List<LearningReportResponse> getAllReports();
    LearningReportResponse getSummary();
}
