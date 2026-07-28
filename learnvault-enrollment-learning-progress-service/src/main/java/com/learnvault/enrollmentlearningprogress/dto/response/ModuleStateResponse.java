package com.learnvault.enrollmentlearningprogress.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleStateResponse {
    private Integer moduleId;
    private String title;
    private Integer sequenceOrder;
    private String contentType;
    private String contentURL;
    private Integer durationMinutes;

    // NOT_STARTED | IN_PROGRESS | ASSESSMENT_PENDING | PASSED | FAILED | COMPLETED | LOCKED
    private String status;
    private boolean locked;

    private boolean hasAssessment;
    private Integer assessmentId;
    private boolean assessmentPassed;
    private int assessmentAttemptsUsed;
    private Integer assessmentMaxAttempts;
}
