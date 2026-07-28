package com.learnvault.enrollmentlearningprogress.service.impl;

import com.learnvault.enrollmentlearningprogress.client.AssessmentClient;
import com.learnvault.enrollmentlearningprogress.client.CertificationBadgeClient;
import com.learnvault.enrollmentlearningprogress.client.CourseCatalogClient;
import com.learnvault.enrollmentlearningprogress.client.NotificationAlertClient;
import com.learnvault.enrollmentlearningprogress.client.dto.AssessmentDto;
import com.learnvault.enrollmentlearningprogress.client.dto.AttemptDto;
import com.learnvault.enrollmentlearningprogress.client.dto.ModuleDto;
import com.learnvault.enrollmentlearningprogress.dto.request.CertificationRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.EnrollmentRequest;
import com.learnvault.enrollmentlearningprogress.dto.request.NotificationRequest;
import com.learnvault.enrollmentlearningprogress.dto.response.EnrollmentResponse;
import com.learnvault.enrollmentlearningprogress.dto.response.LearningStateResponse;
import com.learnvault.enrollmentlearningprogress.dto.response.ModuleStateResponse;
import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.ModuleProgress;
import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import com.learnvault.enrollmentlearningprogress.exception.BadRequestException;
import com.learnvault.enrollmentlearningprogress.exception.ResourceNotFoundException;
import com.learnvault.enrollmentlearningprogress.repository.EnrollmentRepository;
import com.learnvault.enrollmentlearningprogress.repository.ModuleProgressRepository;
import com.learnvault.enrollmentlearningprogress.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final CertificationBadgeClient certificationBadgeClient;
    private final NotificationAlertClient notificationAlertClient;
    private final CourseCatalogClient courseCatalogClient;
    private final AssessmentClient assessmentClient;

    @Override
    @Transactional
    public EnrollmentResponse enrollLearner(EnrollmentRequest request) {
        log.info("Enrolling learner {} in course {}", request.getLearnerId(), request.getCourseId());

        enrollmentRepository.findByLearnerIdAndCourseId(request.getLearnerId(), request.getCourseId())
                .ifPresent(e -> {
                    throw new BadRequestException("Learner already enrolled in this course");
                });

        Enrollment enrollment = Enrollment.builder()
                .learnerId(request.getLearnerId())
                .courseId(request.getCourseId())
                .deadlineDate(request.getDeadlineDate())
                .status(EnrollmentStatus.ENROLLED)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Create module progress entries (placeholder - in real scenario fetch modules from CCM)
        // For now, auto-create a dummy entry or leave for manual creation

        // Send notification
        notificationAlertClient.sendNotification(NotificationRequest.builder()
                .userId(request.getLearnerId())
                .message("You have been enrolled in course " + request.getCourseId())
                .category("ENROLLMENT")
                .build());

        return mapToResponse(saved);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Integer id) {
        log.info("Fetching enrollment by ID: {}", id);
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapToResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByLearner(Integer learnerId) {
        log.info("Fetching enrollments for learner: {}", learnerId);
        return enrollmentRepository.findByLearnerId(learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByCourse(Integer courseId) {
        log.info("Fetching enrollments for course: {}", courseId);
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Integer enrollmentId, Integer moduleId, Integer percent, Integer timeSpentMinutes) {
        log.info("Updating progress for enrollment: {}, module: {}", enrollmentId, moduleId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        ModuleProgress progress = moduleProgressRepository
                .findByEnrollment_EnrollmentIdAndModuleId(enrollmentId, moduleId)
                .orElseGet(() -> ModuleProgress.builder()
                        .enrollment(enrollment)
                        .moduleId(moduleId)
                        .status(ModuleProgressStatus.INPROGRESS)
                        .startedDate(LocalDate.now())
                        .build());

        progress.setTimeSpentMinutes(progress.getTimeSpentMinutes() + (timeSpentMinutes != null ? timeSpentMinutes : 0));
        moduleProgressRepository.save(progress);

        enrollment.setProgressPercent(percent);
        if (percent >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        } else {
            enrollment.setStatus(EnrollmentStatus.INPROGRESS);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EnrollmentResponse completeCourse(Integer enrollmentId) {
        log.info("Completing course for enrollment: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        finalizeCompletion(enrollment);
        return mapToResponse(enrollment);
    }

    // ---------------------------------------------------------------------
    // Sequential learning flow
    // ---------------------------------------------------------------------

   
    @Override
    public LearningStateResponse getLearningState(Integer enrollmentId) {

        try {

            Enrollment enrollment =
                    enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Enrollment not found"));

            return buildState(enrollment, false);

        } catch (Exception ex) {

            ex.printStackTrace();

            throw ex;
        }
    }

    @Override
    @Transactional
    public LearningStateResponse refreshLearningState(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        return buildState(enrollment, true);
    }

    @Override
    @Transactional
    public LearningStateResponse completeModule(Integer enrollmentId, Integer moduleId) {
        log.info("Completing module {} for enrollment {}", moduleId, enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        // Compute current state to validate sequencing (module must exist and be unlocked)
        LearningStateResponse current = buildState(enrollment, false);
        ModuleStateResponse target = current.getModules().stream()
                .filter(m -> m.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Module " + moduleId + " does not belong to this course"));

        if (target.isLocked()) {
            throw new BadRequestException(
                    "Module is locked. Complete the previous modules and pass their assessments first.");
        }

        // Upsert the module progress row as COMPLETED (video content watched / marked complete)
        ModuleProgress progress = moduleProgressRepository
                .findByEnrollment_EnrollmentIdAndModuleId(enrollmentId, moduleId)
                .orElseGet(() -> ModuleProgress.builder()
                        .enrollment(enrollment)
                        .moduleId(moduleId)
                        .startedDate(LocalDate.now())
                        .build());
        if (progress.getStartedDate() == null) {
            progress.setStartedDate(LocalDate.now());
        }
        progress.setStatus(ModuleProgressStatus.COMPLETED);
        progress.setCompletedDate(LocalDate.now());
        moduleProgressRepository.save(progress);

        sendNotification(enrollment.getLearnerId(),
                "Module \"" + safeTitle(target) + "\" completed successfully.",
                "MODULE_COMPLETION");

        // Recompute + auto-complete the course if every module is cleared
        return buildState(enrollment, true);
    }

    private String safeTitle(ModuleStateResponse m) {
        return m.getTitle() != null ? m.getTitle() : ("#" + m.getModuleId());
    }

    /**
     * Builds the per-module learning state. When finalizeIfComplete is true, this also
     * persists the enrollment's progress and finalizes the course (certificate + notifications)
     * once every module is cleared (video completed AND mandatory assessments passed).
     */
    private LearningStateResponse buildState(Enrollment enrollment, boolean finalizeIfComplete) {
        Integer enrollmentId = enrollment.getEnrollmentId();
        Integer courseId = enrollment.getCourseId();
        Integer learnerId = enrollment.getLearnerId();

        List<ModuleDto> modules = new ArrayList<>();
        try {
            List<ModuleDto> fetched = courseCatalogClient.getModules(courseId);
            if (fetched != null) {
                modules = fetched;
            }
        } catch (Exception ex) {
            log.warn("Could not fetch modules for course {}: {}", courseId, ex.getMessage());
        }
        modules.sort(Comparator.comparing(
                m -> m.getSequenceOrder() != null ? m.getSequenceOrder() : Integer.MAX_VALUE));

        Map<Integer, ModuleProgressStatus> progressByModule = new HashMap<>();
        for (ModuleProgress mp : moduleProgressRepository.findByEnrollment_EnrollmentId(enrollmentId)) {
            progressByModule.put(mp.getModuleId(), mp.getStatus());
        }

        Map<Integer, List<AssessmentDto>> assessmentsByModule = new HashMap<>();
        try {
            List<AssessmentDto> assessments = assessmentClient.getAssessmentsByCourse(courseId);
            if (assessments != null) {
                for (AssessmentDto a : assessments) {
                    if (a.getModuleId() != null) {
                        assessmentsByModule.computeIfAbsent(a.getModuleId(), k -> new ArrayList<>()).add(a);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Could not fetch assessments for course {}: {}", courseId, ex.getMessage());
        }

        List<ModuleStateResponse> moduleStates = new ArrayList<>();
        boolean allPreviousCleared = true;
        int completedCount = 0;
        int lockedCount = 0;
        int pendingAssessmentCount = 0;
        Integer currentModuleId = null;

        for (ModuleDto m : modules) {
            ModuleProgressStatus ps = progressByModule.getOrDefault(m.getModuleId(), ModuleProgressStatus.NOTSTARTED);
            boolean moduleDone = ps == ModuleProgressStatus.COMPLETED;

            List<AssessmentDto> mAssessments = assessmentsByModule.getOrDefault(m.getModuleId(), Collections.emptyList());
            boolean hasAssessment = !mAssessments.isEmpty();
            AssessmentDto primary = hasAssessment ? mAssessments.get(0) : null;

            boolean assessmentPassed = true;
            int attemptsUsed = 0;
            if (hasAssessment) {
                for (AssessmentDto a : mAssessments) {
                    List<AttemptDto> attempts = safeGetAttempts(a.getAssessmentId(), learnerId);
                    attemptsUsed += attempts.size();
                    boolean thisPassed = attempts.stream().anyMatch(at -> Boolean.TRUE.equals(at.getPassed()));
                    if (!thisPassed) {
                        assessmentPassed = false;
                    }
                }
            }

            boolean cleared = moduleDone && assessmentPassed;
            boolean locked = !allPreviousCleared;

            String status;
            if (locked) {
                status = "LOCKED";
            } else if (cleared) {
                status = "COMPLETED";
            } else if (moduleDone && hasAssessment && !assessmentPassed) {
                status = attemptsUsed > 0 ? "FAILED" : "ASSESSMENT_PENDING";
            } else if (ps == ModuleProgressStatus.INPROGRESS) {
                status = "IN_PROGRESS";
            } else {
                status = "NOT_STARTED";
            }

            if (cleared) {
                completedCount++;
            }
            if (locked) {
                lockedCount++;
            }
            if (!locked && moduleDone && hasAssessment && !assessmentPassed) {
                pendingAssessmentCount++;
            }
            if (!locked && !cleared && currentModuleId == null) {
                currentModuleId = m.getModuleId();
            }

            moduleStates.add(ModuleStateResponse.builder()
                    .moduleId(m.getModuleId())
                    .title(m.getTitle())
                    .sequenceOrder(m.getSequenceOrder())
                    .contentType(m.getContentType())
                    .contentURL(m.getContentURL())
                    .durationMinutes(m.getDurationMinutes())
                    .status(status)
                    .locked(locked)
                    .hasAssessment(hasAssessment)
                    .assessmentId(primary != null ? primary.getAssessmentId() : null)
                    .assessmentPassed(hasAssessment && assessmentPassed)
                    .assessmentAttemptsUsed(attemptsUsed)
                    .assessmentMaxAttempts(primary != null ? primary.getMaxAttempts() : null)
                    .build());

            allPreviousCleared = allPreviousCleared && cleared;
        }

        int total = modules.size();
        boolean allCleared = total > 0 && completedCount == total;
        int computedProgress = total > 0 ? (int) Math.round((completedCount * 100.0) / total) : 0;

        Integer nextModuleId = null;
        if (currentModuleId != null) {
            for (int i = 0; i < moduleStates.size(); i++) {
                if (moduleStates.get(i).getModuleId().equals(currentModuleId) && i + 1 < moduleStates.size()) {
                    nextModuleId = moduleStates.get(i + 1).getModuleId();
                    break;
                }
            }
        }

        boolean alreadyCompleted = enrollment.getStatus() == EnrollmentStatus.COMPLETED;

        if (finalizeIfComplete) {
            if (allCleared && !alreadyCompleted) {
                finalizeCompletion(enrollment);
            } else if (!alreadyCompleted) {
                enrollment.setProgressPercent(computedProgress);
                if (computedProgress > 0) {
                    enrollment.setStatus(EnrollmentStatus.INPROGRESS);
                }
                enrollmentRepository.save(enrollment);
            }
        }

        boolean completed = enrollment.getStatus() == EnrollmentStatus.COMPLETED || alreadyCompleted;
        EnrollmentStatus displayStatus = completed
                ? EnrollmentStatus.COMPLETED
                : (computedProgress > 0 ? EnrollmentStatus.INPROGRESS : enrollment.getStatus());
        int displayProgress = completed ? 100 : computedProgress;

        return LearningStateResponse.builder()
                .enrollmentId(enrollmentId)
                .courseId(courseId)
                .learnerId(learnerId)
                .status(displayStatus)
                .progressPercent(displayProgress)
                .completionDate(enrollment.getCompletionDate())
                .totalModules(total)
                .completedModules(completedCount)
                .lockedModules(lockedCount)
                .pendingAssessments(pendingAssessmentCount)
                .currentModuleId(currentModuleId)
                .nextModuleId(nextModuleId)
                .courseCompleted(completed || allCleared)
                .modules(moduleStates)
                .build();
    }

    private List<AttemptDto> safeGetAttempts(Integer assessmentId, Integer learnerId) {
        try {
            List<AttemptDto> attempts = assessmentClient.getAttempts(assessmentId, learnerId);
            return attempts != null ? attempts : Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Could not fetch attempts for assessment {} learner {}: {}", assessmentId, learnerId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    /** Marks the enrollment completed and fires certificate + completion notifications (idempotent-ish). */
    private void finalizeCompletion(Enrollment enrollment) {
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletionDate(LocalDate.now());
        enrollment.setProgressPercent(100);
        enrollmentRepository.save(enrollment);

        sendNotification(enrollment.getLearnerId(),
                "Congratulations! You have completed course " + enrollment.getCourseId() + " successfully.",
                "COURSE_COMPLETION");

        try {
            certificationBadgeClient.issueCertificate(CertificationRequest.builder()
                    .courseId(enrollment.getCourseId())
                    .learnerId(enrollment.getLearnerId())
                    .build());
            sendNotification(enrollment.getLearnerId(),
                    "Your certificate for course " + enrollment.getCourseId()
                            + " has been generated and is available for download.",
                    "CERTIFICATION");
        } catch (Exception ex) {
            log.warn("Certificate issuance failed for enrollment {}: {}", enrollment.getEnrollmentId(), ex.getMessage());
        }
    }

    private void sendNotification(Integer userId, String message, String category) {
        if (userId == null) {
            return;
        }
        try {
            notificationAlertClient.sendNotification(NotificationRequest.builder()
                    .userId(userId)
                    .message(message)
                    .category(category)
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to send {} notification to user {}: {}", category, userId, ex.getMessage());
        }
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .learnerId(enrollment.getLearnerId())
                .courseId(enrollment.getCourseId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .deadlineDate(enrollment.getDeadlineDate())
                .completionDate(enrollment.getCompletionDate())
                .progressPercent(enrollment.getProgressPercent())
                .status(enrollment.getStatus())
                .build();
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        log.info("Fetching all enrollments");
        return enrollmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}