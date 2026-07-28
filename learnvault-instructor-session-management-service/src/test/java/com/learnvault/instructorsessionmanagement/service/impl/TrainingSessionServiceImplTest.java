package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.CourseClient;
import com.learnvault.instructorsessionmanagement.client.EnrollmentClient;
import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.client.dto.CourseDto;
import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionType;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import com.learnvault.instructorsessionmanagement.repository.SessionFeedbackRepository;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceImplTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private InstructorRepository instructorRepository;
    @Mock
    private SessionRegistrationRepository sessionRegistrationRepository;
    @Mock
    private SessionFeedbackRepository sessionFeedbackRepository;
    @Mock
    private CourseClient courseClient;
    @Mock
    private EnrollmentClient enrollmentClient;
    @Mock
    private NotificationClient notificationClient;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private TrainingSessionServiceImpl sessionService;

    private TrainingSessionRequest onlineRequest() {
        return TrainingSessionRequest.builder()
                .courseId(100).instructorId(1).title("Intro").sessionType(SessionType.ONLINE)
                .meetingLink("http://meet").sessionDate(LocalDate.now())
                .startTime("10:00").endTime("11:00").maxCapacity(20).build();
    }

    @Test
    void scheduleSession_success_savesAndReturnsResponse() {
        // Arrange
        TrainingSessionRequest request = onlineRequest();
        Instructor instructor = Instructor.builder().instructorId(1).userId(50).build();
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        CourseDto course = new CourseDto();
        course.setInstructorId(1); // instructor is assigned to this course
        course.setTitle("Java 101");
        when(courseClient.getCourseById(100)).thenReturn(course);
        TrainingSession saved = TrainingSession.builder()
                .sessionId(5).courseId(100).instructor(instructor).title("Intro").maxCapacity(20)
                .sessionType(SessionType.ONLINE).sessionDate(LocalDate.now())
                .startTime(Time.valueOf("10:00:00")).endTime(Time.valueOf("11:00:00"))
                .status(SessionStatus.SCHEDULED).build();
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(saved);
        when(enrollmentClient.getByCourse(100)).thenReturn(List.of());
        when(sessionRegistrationRepository.countBySession_SessionIdAndAttendanceStatusNot(any(), any())).thenReturn(0L);

        // Act
        TrainingSessionResponse response = sessionService.scheduleSession(request);

        // Assert
        assertEquals(5, response.getSessionId());
        assertEquals(SessionStatus.SCHEDULED, response.getStatus());
        verify(trainingSessionRepository).save(any(TrainingSession.class));
    }

    @Test
    void scheduleSession_instructorNotFound_throwsResourceNotFound() {
        // Arrange
        TrainingSessionRequest request = onlineRequest();
        when(instructorRepository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> sessionService.scheduleSession(request));
    }

    @Test
    void scheduleSession_instructorNotAssignedToCourse_throwsBadRequest() {
        // Arrange
        TrainingSessionRequest request = onlineRequest();
        Instructor instructor = Instructor.builder().instructorId(1).userId(50).build();
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        CourseDto course = new CourseDto();
        course.setInstructorId(999); // different instructor owns the course
        when(courseClient.getCourseById(100)).thenReturn(course);

        // Act + Assert
        assertThrows(BadRequestException.class, () -> sessionService.scheduleSession(request));
        verify(trainingSessionRepository, never()).save(any(TrainingSession.class));
    }
}
