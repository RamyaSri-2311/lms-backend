package com.learnvault.learninganalyticsreporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningReportResponse {
    private Integer reportId;
    private String scope;
    private String metrics;
    private LocalDateTime generatedDate;
}