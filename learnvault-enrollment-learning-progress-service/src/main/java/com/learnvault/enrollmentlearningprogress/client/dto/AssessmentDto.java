package com.learnvault.enrollmentlearningprogress.client.dto;

import lombok.Data;

/** Assessment as returned by the assessment-evaluation service (subset). */
@Data
public class AssessmentDto {
    private Integer assessmentId;
    private Integer courseId;
    private Integer moduleId;
    private Integer passingMarks;
    private Integer maxAttempts;
}
