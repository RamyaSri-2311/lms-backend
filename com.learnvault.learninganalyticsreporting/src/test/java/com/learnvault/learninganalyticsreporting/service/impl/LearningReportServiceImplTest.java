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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LearningReportServiceImplTest {

    @Mock
    private LearningReportRepository learningReportRepository;
    @Mock
    private EnrollmentLearningProgressClient enrollmentClient;
    @Mock
    private AssessmentEvaluationClient assessmentClient;
    @Mock
    private IdentityAccessManagementClient identityClient;

    @InjectMocks
    private LearningReportServiceImpl learningReportService;

    @Test
    void generateReport_success_savesAndReturnsResponse() {
        // Arrange
        LearningReportRequest request = LearningReportRequest.builder()
                .scope("CUSTOM")
                .build();
        LearningReport saved = LearningReport.builder()
                .reportId(1)
                .scope("CUSTOM")
                .metrics("{\"message\": \"Custom report scope\"}")
                .build();
        when(learningReportRepository.save(any(LearningReport.class))).thenReturn(saved);

        // Act
        LearningReportResponse response = learningReportService.generateReport(request);

        // Assert
        assertEquals(1, response.getReportId());
        assertEquals("CUSTOM", response.getScope());
        verify(learningReportRepository).save(any(LearningReport.class));
    }

    @Test
    void getReportById_notFound_throwsResourceNotFoundException() {
        // Arrange
        when(learningReportRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> learningReportService.getReportById(99));
    }

    @Test
    void getSummary_success_returnsAggregatedTotals() {
        // Arrange
        when(identityClient.getAllUsers()).thenReturn(List.of(Map.of("id", 1)));
        when(enrollmentClient.getAllEnrollments()).thenReturn(List.of(Map.of("id", 1), Map.of("id", 2)));
        when(assessmentClient.getAllAssessments()).thenReturn(List.of(Map.of("id", 1)));
        when(assessmentClient.getAllAttempts()).thenReturn(List.of(Map.of("score", 80), Map.of("score", 100)));

        // Act
        ReportSummaryResponse summary = learningReportService.getSummary();

        // Assert
        assertEquals(1, summary.getTotalUsers());
        assertEquals(2, summary.getTotalEnrollments());
        assertEquals(1, summary.getTotalAssessments());
        assertEquals(2, summary.getTotalAttempts());
        assertEquals(90.0, summary.getAverageScore());
    }
}
