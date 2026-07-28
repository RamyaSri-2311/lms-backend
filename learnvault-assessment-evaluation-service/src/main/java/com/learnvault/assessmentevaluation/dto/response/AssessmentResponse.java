package com.learnvault.assessmentevaluation.dto.response;

import com.learnvault.assessmentevaluation.entity.enums.AssessmentType;
import com.learnvault.assessmentevaluation.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {
    private Integer assessmentId;
    private Integer courseId;
    private Integer moduleId;
    private AssessmentType type;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer maxAttempts;
    private Integer timeLimitMinutes;
    private Status status;
}