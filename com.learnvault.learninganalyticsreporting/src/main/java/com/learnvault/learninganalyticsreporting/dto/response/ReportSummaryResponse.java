package com.learnvault.learninganalyticsreporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryResponse {
    private Integer totalUsers;
    private Integer totalEnrollments;
    private Integer totalAssessments;
    private Integer totalAttempts;
    private Double completionRate;
    private Double averageScore;
}