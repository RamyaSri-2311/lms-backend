package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionFeedbackResponse {
    private Integer feedbackId;
    private Integer sessionId;
    private Integer learnerId;
    private String learnerName;
    private Integer rating;
    private Integer sessionQuality;
    private Integer instructorKnowledge;
    private Integer contentRelevance;
    private String comments;
    private LocalDateTime submittedDate;
}
