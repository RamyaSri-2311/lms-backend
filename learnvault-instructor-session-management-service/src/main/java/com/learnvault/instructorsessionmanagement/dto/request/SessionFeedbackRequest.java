package com.learnvault.instructorsessionmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionFeedbackRequest {
    private Integer sessionId;
    private Integer learnerId;
    private Integer rating;              // 1-5
    private Integer sessionQuality;      // 1-5
    private Integer instructorKnowledge; // 1-5
    private Integer contentRelevance;    // 1-5
    private String comments;
}
