package com.learnvault.enrollmentlearningprogress.service.impl;

import com.learnvault.enrollmentlearningprogress.client.AssessmentClient;
import com.learnvault.enrollmentlearningprogress.client.CertificationBadgeClient;
import com.learnvault.enrollmentlearningprogress.client.CourseCatalogClient;
import com.learnvault.enrollmentlearningprogress.client.NotificationAlertClient;
import com.learnvault.enrollmentlearningprogress.dto.request.EnrollmentRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.NotificationRequest;
import com.learnvault.enrollmentlearningprogress.dto.response.EnrollmentResponse;
import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import com.learnvault.enrollmentlearningprogress.exception.BadRequestException;
import com.learnvault.enrollmentlearningprogress.exception.ResourceNotFoundException;
import com.learnvault.enrollmentlearningprogress.repository.EnrollmentRepository;
import com.learnvault.enrollmentlearningprogress.repository.ModuleProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private ModuleProgressRepository moduleProgressRepository;
    @Mock
    private CertificationBadgeClient certificationBadgeClient;
    @Mock
    private NotificationAlertClient notificationAlertClient;
    @Mock
    private CourseCatalogClient courseCatalogClient;
    @Mock
    private AssessmentClient assessmentClient;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    void enrollLearner_success_savesAndNotifies() {
        // Arrange
        EnrollmentRequest request = EnrollmentRequest.builder()
                .learnerId(1).courseId(2).build();
        Enrollment saved = Enrollment.builder()
                .enrollmentId(10).learnerId(1).courseId(2)
                .status(EnrollmentStatus.ENROLLED).build();
        when(enrollmentRepository.findByLearnerIdAndCourseId(1, 2)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        // Act
        EnrollmentResponse response = enrollmentService.enrollLearner(request);

        // Assert
        assertEquals(10, response.getEnrollmentId());
        assertEquals(EnrollmentStatus.ENROLLED, response.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(notificationAlertClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void getEnrollmentById_notFound_throwsResourceNotFound() {
        // Arrange
        when(enrollmentRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(99));
    }

    @Test
    void enrollLearner_alreadyEnrolled_throwsBadRequest() {
        // Arrange
        EnrollmentRequest request = EnrollmentRequest.builder()
                .learnerId(1).courseId(2).build();
        Enrollment existing = Enrollment.builder()
                .enrollmentId(5).learnerId(1).courseId(2).build();
        when(enrollmentRepository.findByLearnerIdAndCourseId(1, 2)).thenReturn(Optional.of(existing));

        // Act + Assert
        assertThrows(BadRequestException.class, () -> enrollmentService.enrollLearner(request));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}
