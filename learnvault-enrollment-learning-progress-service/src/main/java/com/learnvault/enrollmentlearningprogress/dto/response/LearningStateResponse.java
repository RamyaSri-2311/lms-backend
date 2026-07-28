package com.learnvault.enrollmentlearningprogress.dto.response;

import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStateResponse {
    private Integer enrollmentId;
    private Integer courseId;
    private Integer learnerId;
    private EnrollmentStatus status;
    private Integer progressPercent;
    private LocalDate completionDate;

    private int totalModules;
    private int completedModules;
    private int lockedModules;
    private int pendingAssessments;

    private Integer currentModuleId;
    private Integer nextModuleId;

    private boolean courseCompleted;

    private List<ModuleStateResponse> modules;
}
