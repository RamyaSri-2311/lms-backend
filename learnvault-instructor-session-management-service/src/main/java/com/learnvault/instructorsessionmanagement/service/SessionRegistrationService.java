package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;

import java.util.List;

public interface SessionRegistrationService {
    SessionRegistrationResponse registerLearner(SessionRegistrationRequest request);
    List<SessionRegistrationResponse> getRegistrationsBySession(Integer sessionId);
    List<SessionRegistrationResponse> getRegistrationsByLearner(Integer learnerId);
    SessionRegistrationResponse cancelRegistration(Integer id);
    SessionRegistrationResponse markAttendance(Integer id, String status);
}
