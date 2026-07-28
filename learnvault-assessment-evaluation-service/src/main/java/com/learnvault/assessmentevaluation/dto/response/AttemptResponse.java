package com.learnvault.assessmentevaluation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {
    private Integer attemptId;
    private Integer assessmentId;
    private Integer learnerId;
    private Integer attemptNumber;
    private Integer score;
    private Boolean passed;
    private LocalDateTime attemptDate;
    private Integer timeTakenMinutes;
}