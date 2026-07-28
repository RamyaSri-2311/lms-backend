package com.learnvault.enrollmentlearningprogress.client.dto;

import lombok.Data;

/** Attempt record as returned by the assessment-evaluation service (subset). */
@Data
public class AttemptDto {
    private Integer attemptId;
    private Integer assessmentId;
    private Integer learnerId;
    private Integer score;
    private Boolean passed;
}
