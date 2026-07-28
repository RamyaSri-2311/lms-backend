package com.learnvault.assessmentevaluation.service;

import com.learnvault.assessmentevaluation.dto.request.AssessmentRequest;
import com.learnvault.assessmentevaluation.dto.response.AssessmentResponse;

import java.util.List;

public interface AssessmentService {
    AssessmentResponse createAssessment(AssessmentRequest request);
    AssessmentResponse getAssessmentById(Integer id);
    List<AssessmentResponse> getAllAssessments(Integer courseId, Integer moduleId);
    AssessmentResponse updateStatus(Integer id, String status);
}