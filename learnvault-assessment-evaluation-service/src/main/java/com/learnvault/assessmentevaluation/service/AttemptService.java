package com.learnvault.assessmentevaluation.service;

import com.learnvault.assessmentevaluation.dto.request.AttemptRequest;
import com.learnvault.assessmentevaluation.dto.response.AttemptCountResponse;
import com.learnvault.assessmentevaluation.dto.response.AttemptResponse;

import java.util.List;

public interface AttemptService {
    AttemptResponse submitAttempt(AttemptRequest request);
    List<AttemptResponse> getAttemptHistory(Integer assessmentId, Integer learnerId);
    AttemptCountResponse getAttemptCount(Integer assessmentId, Integer learnerId);
    List<AttemptResponse> getAllAttempts();
}