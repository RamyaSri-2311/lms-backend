package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearnerSessionStatsResponse {
    private long upcomingSessions;
    private long registeredSessions;
    private long completedSessions;
    private long pendingFeedback;
    private double attendancePercentage;
}
