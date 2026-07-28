package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.LearnerSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.SessionAnalyticsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;

import java.util.List;

public interface TrainingSessionService {
    TrainingSessionResponse scheduleSession(TrainingSessionRequest request);
    TrainingSessionResponse getSessionById(Integer id);
    List<TrainingSessionResponse> getAllSessions(Integer courseId, Integer instructorId);
    List<TrainingSessionResponse> getSessionsForLearner(Integer learnerId);
    TrainingSessionResponse startSession(Integer id);
    TrainingSessionResponse completeSession(Integer id);
    TrainingSessionResponse cancelSession(Integer id);
    CapacityResponse getCapacity(Integer id);
    SessionAnalyticsResponse getAnalytics(Integer sessionId);
    InstructorSessionStatsResponse getInstructorStats(Integer instructorId);
    LearnerSessionStatsResponse getLearnerStats(Integer learnerId);
}
