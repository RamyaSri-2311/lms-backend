package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.client.NotificationClient;
import com.learnvault.assessmentevaluation.dto.request.AttemptRequest;
import com.learnvault.assessmentevaluation.dto.response.AttemptResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.AttemptRecord;
import com.learnvault.assessmentevaluation.entity.enums.AssessmentType;
import com.learnvault.assessmentevaluation.entity.enums.Status;
import com.learnvault.assessmentevaluation.exception.BadRequestException;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.repository.AttemptRepository;
import com.learnvault.assessmentevaluation.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptServiceImplTest {

    @Mock
    private AttemptRepository attemptRepository;
    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private AttemptServiceImpl attemptService;

    private Assessment buildAssessment() {
        return Assessment.builder()
                .assessmentId(1).courseId(10).moduleId(20).type(AssessmentType.QUIZ)
                .totalMarks(100).passingMarks(40).maxAttempts(3).timeLimitMinutes(30)
                .status(Status.ACTIVE).build();
    }

    @Test
    void submitAttempt_success_savesAndReturnsResponse() {
        // Arrange
        Assessment assessment = buildAssessment();
        AttemptRequest request = AttemptRequest.builder()
                .assessmentId(1).learnerId(5).score(50).timeTakenMinutes(15).build();
        AttemptRecord saved = AttemptRecord.builder()
                .attemptId(100).assessment(assessment).learnerId(5)
                .attemptNumber(1).score(50).passed(true).timeTakenMinutes(15).build();
        when(assessmentRepository.findById(1)).thenReturn(Optional.of(assessment));
        when(attemptRepository.countByAssessment_AssessmentIdAndLearnerId(1, 5)).thenReturn(0L);
        when(attemptRepository.save(any(AttemptRecord.class))).thenReturn(saved);

        // Act
        AttemptResponse response = attemptService.submitAttempt(request);

        // Assert
        assertEquals(50, response.getScore());
        assertEquals(true, response.getPassed());
        verify(attemptRepository).save(any(AttemptRecord.class));
    }

    @Test
    void submitAttempt_assessmentNotFound_throwsResourceNotFoundException() {
        // Arrange
        AttemptRequest request = AttemptRequest.builder()
                .assessmentId(99).learnerId(5).build();
        when(assessmentRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> attemptService.submitAttempt(request));
    }

    @Test
    void submitAttempt_maxAttemptsExceeded_throwsBadRequestException() {
        // Arrange
        Assessment assessment = buildAssessment();
        AttemptRequest request = AttemptRequest.builder()
                .assessmentId(1).learnerId(5).score(50).build();
        when(assessmentRepository.findById(1)).thenReturn(Optional.of(assessment));
        when(attemptRepository.countByAssessment_AssessmentIdAndLearnerId(1, 5)).thenReturn(3L);

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> attemptService.submitAttempt(request));
    }
}
