package com.learnvault.assessmentevaluation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptRequest {
    private Integer assessmentId;
    private Integer learnerId;
    // Preferred: map of questionId -> selected answer text. When present, the server grades
    // authoritatively against Question.correctAnswer (trimmed, case-insensitive).
    private Map<Integer, String> answers;
    // Legacy fallback: a pre-computed score. Only used when `answers` is null/empty.
    private Integer score;
    private Integer timeTakenMinutes;
}
