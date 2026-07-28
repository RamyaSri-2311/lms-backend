package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.dto.request.LearningReportRequest;
import com.learnvault.identityaccessmanagement.dto.response.LearningReportResponse;
import com.learnvault.identityaccessmanagement.entity.LearningReport;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.LearningReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningReportServiceImplTest {

    @Mock
    private LearningReportRepository learningReportRepository;

    @InjectMocks
    private LearningReportServiceImpl learningReportService;

    // Helper to build a sample saved report
    private LearningReport sampleReport() {
        return LearningReport.builder()
                .reportId(1)
                .scope("COURSE")
                .metrics("{\"scope\":\"COURSE\"}")
                .build();
    }

    @Test
    void generateReport_success() {
        // Arrange
        LearningReportRequest request = LearningReportRequest.builder()
                .scope("COURSE").build();
        when(learningReportRepository.save(any(LearningReport.class))).thenReturn(sampleReport());

        // Act
        LearningReportResponse response = learningReportService.generateReport(request);

        // Assert
        assertEquals("COURSE", response.getScope());
        assertEquals(1, response.getReportId());
        verify(learningReportRepository).save(any(LearningReport.class));
    }

    @Test
    void getReportById_notFound_throws() {
        // Arrange
        when(learningReportRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> learningReportService.getReportById(99));
    }

    @Test
    void getSummary_success_savesReport() {
        // Arrange - no business exception path exists, so a second success/verify test
        when(learningReportRepository.save(any(LearningReport.class))).thenReturn(sampleReport());

        // Act
        LearningReportResponse response = learningReportService.getSummary();

        // Assert
        assertEquals("COURSE", response.getScope());
        verify(learningReportRepository).save(any(LearningReport.class));
    }
}
