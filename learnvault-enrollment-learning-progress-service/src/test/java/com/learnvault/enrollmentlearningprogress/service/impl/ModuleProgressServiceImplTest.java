package com.learnvault.enrollmentlearningprogress.service.impl;

import com.learnvault.enrollmentlearningprogress.dto.response.ModuleProgressResponse;
import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.ModuleProgress;
import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import com.learnvault.enrollmentlearningprogress.exception.ResourceNotFoundException;
import com.learnvault.enrollmentlearningprogress.repository.EnrollmentRepository;
import com.learnvault.enrollmentlearningprogress.repository.ModuleProgressRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleProgressServiceImplTest {

    @Mock
    private ModuleProgressRepository moduleProgressRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private ModuleProgressServiceImpl moduleProgressService;

    @Test
    void updateModuleStatus_success_savesCompletedProgress() {
        // Arrange
        Enrollment enrollment = Enrollment.builder().enrollmentId(1).learnerId(7).courseId(3).build();
        ModuleProgress progress = ModuleProgress.builder()
                .progressId(100).enrollment(enrollment).moduleId(5)
                .status(ModuleProgressStatus.INPROGRESS).build();
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment));
        when(moduleProgressRepository.findByEnrollment_EnrollmentIdAndModuleId(1, 5))
                .thenReturn(Optional.of(progress));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenReturn(progress);

        // Act
        ModuleProgressResponse response =
                moduleProgressService.updateModuleStatus(1, 5, "COMPLETED", 15);

        // Assert
        assertEquals(ModuleProgressStatus.COMPLETED, response.getStatus());
        assertEquals(15, response.getTimeSpentMinutes());
        assertEquals(1, response.getEnrollmentId());
        verify(moduleProgressRepository).save(any(ModuleProgress.class));
    }

    @Test
    void updateModuleStatus_enrollmentNotFound_throwsResourceNotFound() {
        // Arrange
        when(enrollmentRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> moduleProgressService.updateModuleStatus(99, 5, "COMPLETED", 10));
    }

    @Test
    void getProgressByEnrollment_success_returnsMappedList() {
        // Arrange
        Enrollment enrollment = Enrollment.builder().enrollmentId(1).build();
        ModuleProgress progress = ModuleProgress.builder()
                .progressId(100).enrollment(enrollment).moduleId(5)
                .status(ModuleProgressStatus.NOTSTARTED).build();
        when(moduleProgressRepository.findByEnrollment_EnrollmentId(1))
                .thenReturn(List.of(progress));

        // Act
        List<ModuleProgressResponse> result = moduleProgressService.getProgressByEnrollment(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getModuleId());
        assertEquals(ModuleProgressStatus.NOTSTARTED, result.get(0).getStatus());
    }
}
