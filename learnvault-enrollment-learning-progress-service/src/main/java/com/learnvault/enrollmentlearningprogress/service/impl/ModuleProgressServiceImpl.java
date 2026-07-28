package com.learnvault.enrollmentlearningprogress.service.impl;

import com.learnvault.enrollmentlearningprogress.dto.response.ModuleProgressResponse;
import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.ModuleProgress;
import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import com.learnvault.enrollmentlearningprogress.exception.ResourceNotFoundException;
import com.learnvault.enrollmentlearningprogress.repository.EnrollmentRepository;
import com.learnvault.enrollmentlearningprogress.repository.ModuleProgressRepository;
import com.learnvault.enrollmentlearningprogress.service.ModuleProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleProgressServiceImpl implements ModuleProgressService {

    private final ModuleProgressRepository moduleProgressRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<ModuleProgressResponse> getProgressByEnrollment(Integer enrollmentId) {
        log.info("Fetching module progress for enrollment: {}", enrollmentId);
        return moduleProgressRepository.findByEnrollment_EnrollmentId(enrollmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ModuleProgressResponse updateModuleStatus(Integer enrollmentId, Integer moduleId, String status, Integer timeSpentMinutes) {
        log.info("Updating module {} status to {} for enrollment {}", moduleId, status, enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        ModuleProgress progress = moduleProgressRepository
                .findByEnrollment_EnrollmentIdAndModuleId(enrollmentId, moduleId)
                .orElse(ModuleProgress.builder()
                        .enrollment(enrollment)
                        .moduleId(moduleId)
                        .startedDate(LocalDate.now())
                        .build());

        progress.setStatus(ModuleProgressStatus.valueOf(status));
        if (timeSpentMinutes != null) {
            progress.setTimeSpentMinutes(progress.getTimeSpentMinutes() + timeSpentMinutes);
        }
        if (ModuleProgressStatus.COMPLETED.name().equals(status)) {
            progress.setCompletedDate(LocalDate.now());
        }

        ModuleProgress saved = moduleProgressRepository.save(progress);
        return mapToResponse(saved);
    }

    private ModuleProgressResponse mapToResponse(ModuleProgress progress) {
        return ModuleProgressResponse.builder()
                .progressId(progress.getProgressId())
                .enrollmentId(progress.getEnrollment().getEnrollmentId())
                .moduleId(progress.getModuleId())
                .startedDate(progress.getStartedDate())
                .completedDate(progress.getCompletedDate())
                .timeSpentMinutes(progress.getTimeSpentMinutes())
                .status(progress.getStatus())
                .build();
    }
}