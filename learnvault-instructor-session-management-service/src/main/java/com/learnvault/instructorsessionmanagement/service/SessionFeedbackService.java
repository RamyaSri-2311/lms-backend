package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.SessionFeedbackRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionFeedbackResponse;

import java.util.List;

public interface SessionFeedbackService {
    SessionFeedbackResponse submitFeedback(SessionFeedbackRequest request);
    List<SessionFeedbackResponse> getFeedbackBySession(Integer sessionId);
    List<SessionFeedbackResponse> getFeedbackByLearner(Integer learnerId);
    SessionFeedbackResponse getLearnerFeedbackForSession(Integer sessionId, Integer learnerId);
}
