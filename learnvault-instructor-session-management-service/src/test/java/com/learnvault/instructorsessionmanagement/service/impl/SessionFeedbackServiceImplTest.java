package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.request.SessionFeedbackRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionFeedbackResponse;
import com.learnvault.instructorsessionmanagement.entity.SessionFeedback;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.SessionFeedbackRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
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
class SessionFeedbackServiceImplTest {

    @Mock
    private SessionFeedbackRepository sessionFeedbackRepository;
    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private NotificationClient notificationClient;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private SessionFeedbackServiceImpl feedbackService;

    private TrainingSession completedSession() {
        // instructor left null so notifyInstructor short-circuits (best-effort)
        return TrainingSession.builder().sessionId(5).title("Intro").status(SessionStatus.COMPLETED).build();
    }

    @Test
    void submitFeedback_success_savesAndReturnsResponse() {
        // Arrange
        SessionFeedbackRequest request = SessionFeedbackRequest.builder()
                .sessionId(5).learnerId(7).rating(4).sessionQuality(5).comments("Great").build();
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.of(completedSession()));
        when(sessionFeedbackRepository.existsBySessionIdAndLearnerId(5, 7)).thenReturn(false);
        SessionFeedback saved = SessionFeedback.builder()
                .feedbackId(1).sessionId(5).learnerId(7).rating(4).sessionQuality(5).comments("Great").build();
        when(sessionFeedbackRepository.save(any(SessionFeedback.class))).thenReturn(saved);

        // Act
        SessionFeedbackResponse response = feedbackService.submitFeedback(request);

        // Assert
        assertEquals(4, response.getRating());
        assertEquals(7, response.getLearnerId());
        verify(sessionFeedbackRepository).save(any(SessionFeedback.class));
    }

    @Test
    void submitFeedback_sessionNotFound_throwsResourceNotFound() {
        // Arrange
        SessionFeedbackRequest request = SessionFeedbackRequest.builder()
                .sessionId(5).learnerId(7).rating(4).build();
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> feedbackService.submitFeedback(request));
    }

    @Test
    void submitFeedback_sessionNotCompleted_throwsBadRequest() {
        // Arrange
        SessionFeedbackRequest request = SessionFeedbackRequest.builder()
                .sessionId(5).learnerId(7).rating(4).build();
        TrainingSession session = completedSession();
        session.setStatus(SessionStatus.SCHEDULED); // business rule: feedback only after COMPLETED
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.of(session));

        // Act + Assert
        assertThrows(BadRequestException.class, () -> feedbackService.submitFeedback(request));
        verify(sessionFeedbackRepository, never()).save(any(SessionFeedback.class));
    }
}
