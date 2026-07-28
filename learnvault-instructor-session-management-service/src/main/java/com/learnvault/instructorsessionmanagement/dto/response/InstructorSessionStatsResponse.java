package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorSessionStatsResponse {
    private long totalSessions;
    private long upcomingSessions;
    private long completedSessions;
    private long registeredLearners;
    private double attendancePercentage;
    private double averageRating;
}
