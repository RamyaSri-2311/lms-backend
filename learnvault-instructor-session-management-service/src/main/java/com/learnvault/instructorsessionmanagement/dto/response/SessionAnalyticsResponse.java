package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionAnalyticsResponse {
    private Integer sessionId;
    private long registrationCount;
    private long presentCount;
    private long absentCount;
    private double attendancePercentage;
    private double averageRating;
    private long feedbackCount;
    // rating value (1-5) -> number of feedbacks with that rating
    private Map<Integer, Long> ratingDistribution;
}
