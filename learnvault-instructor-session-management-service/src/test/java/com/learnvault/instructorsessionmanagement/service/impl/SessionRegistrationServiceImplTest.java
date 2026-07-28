package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.EnrollmentClient;
import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.client.dto.EnrollmentDto;
import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;
import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionRegistrationServiceImplTest {

    @Mock
    private SessionRegistrationRepository sessionRegistrationRepository;
    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private EnrollmentClient enrollmentClient;
    @Mock
    private NotificationClient notificationClient;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private SessionRegistrationServiceImpl registrationService;

    private TrainingSession scheduledSession() {
        return TrainingSession.builder()
                .sessionId(5).courseId(100).title("Intro").maxCapacity(10)
                .status(SessionStatus.SCHEDULED).build();
    }

    @Test
    void registerLearner_success_savesAndReturnsResponse() {
        // Arrange
        SessionRegistrationRequest request = SessionRegistrationRequest.builder().sessionId(5).learnerId(7).build();
        TrainingSession session = scheduledSession();
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.of(session));
        EnrollmentDto enrollment = new EnrollmentDto();
        enrollment.setCourseId(100);
        when(enrollmentClient.getByLearner(7)).thenReturn(List.of(enrollment));
        when(sessionRegistrationRepository.countBySession_SessionIdAndAttendanceStatusNot(5, AttendanceStatus.CANCELLED)).thenReturn(2L);
        when(sessionRegistrationRepository.findBySession_SessionIdAndLearnerId(5, 7)).thenReturn(Optional.empty());
        SessionRegistration saved = SessionRegistration.builder()
                .registrationId(1).session(session).learnerId(7).attendanceStatus(AttendanceStatus.REGISTERED).build();
        when(sessionRegistrationRepository.save(any(SessionRegistration.class))).thenReturn(saved);

        // Act
        SessionRegistrationResponse response = registrationService.registerLearner(request);

        // Assert
        assertEquals(7, response.getLearnerId());
        assertEquals(AttendanceStatus.REGISTERED, response.getAttendanceStatus());
        verify(sessionRegistrationRepository).save(any(SessionRegistration.class));
    }

    @Test
    void registerLearner_sessionNotFound_throwsResourceNotFound() {
        // Arrange
        SessionRegistrationRequest request = SessionRegistrationRequest.builder().sessionId(5).learnerId(7).build();
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> registrationService.registerLearner(request));
    }

    @Test
    void registerLearner_sessionNotScheduled_throwsBadRequest() {
        // Arrange
        SessionRegistrationRequest request = SessionRegistrationRequest.builder().sessionId(5).learnerId(7).build();
        TrainingSession session = scheduledSession();
        session.setStatus(SessionStatus.COMPLETED); // business rule: only SCHEDULED sessions allow registration
        when(trainingSessionRepository.findById(5)).thenReturn(Optional.of(session));

        // Act + Assert
        assertThrows(BadRequestException.class, () -> registrationService.registerLearner(request));
        verify(sessionRegistrationRepository, never()).save(any(SessionRegistration.class));
    }
}
