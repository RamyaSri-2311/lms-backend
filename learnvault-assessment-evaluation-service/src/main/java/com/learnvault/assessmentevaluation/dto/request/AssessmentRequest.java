package com.learnvault.assessmentevaluation.dto.request;

import com.learnvault.assessmentevaluation.entity.enums.AssessmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequest {
    private Integer courseId;
    private Integer moduleId;
    private AssessmentType type;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer maxAttempts;
    private Integer timeLimitMinutes;
}