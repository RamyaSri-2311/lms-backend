package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.dto.request.AssessmentRequest;
import com.learnvault.assessmentevaluation.dto.response.AssessmentResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.enums.AssessmentType;
import com.learnvault.assessmentevaluation.entity.enums.Status;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
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
class AssessmentServiceImplTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    private Assessment buildAssessment() {
        return Assessment.builder()
                .assessmentId(1)
                .courseId(10)
                .moduleId(20)
                .type(AssessmentType.QUIZ)
                .totalMarks(100)
                .passingMarks(40)
                .maxAttempts(3)
                .timeLimitMinutes(30)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void createAssessment_success_returnsResponse() {
        // Arrange
        AssessmentRequest request = AssessmentRequest.builder()
                .courseId(10).moduleId(20).type(AssessmentType.QUIZ)
                .totalMarks(100).passingMarks(40).maxAttempts(3).timeLimitMinutes(30)
                .build();
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(buildAssessment());

        // Act
        AssessmentResponse response = assessmentService.createAssessment(request);

        // Assert
        assertEquals(1, response.getAssessmentId());
        assertEquals(AssessmentType.QUIZ, response.getType());
        verify(assessmentRepository).save(any(Assessment.class));
    }

    @Test
    void getAssessmentById_notFound_throwsResourceNotFoundException() {
        // Arrange
        when(assessmentRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> assessmentService.getAssessmentById(99));
    }

    @Test
    void updateStatus_success_savesAndReturnsResponse() {
        // Arrange
        Assessment assessment = buildAssessment();
        when(assessmentRepository.findById(1)).thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(assessment);

        // Act
        AssessmentResponse response = assessmentService.updateStatus(1, "INACTIVE");

        // Assert
        assertEquals(Status.INACTIVE, response.getStatus());
        verify(assessmentRepository).save(assessment);
    }
}
