package com.learnvault.enrollmentlearningprogress.service;

import com.learnvault.enrollmentlearningprogress.dto.request.EnrollmentRequest;
import com.learnvault.enrollmentlearningprogress.dto.response.EnrollmentResponse;
import com.learnvault.enrollmentlearningprogress.dto.response.LearningStateResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollLearner(EnrollmentRequest request);
    EnrollmentResponse getEnrollmentById(Integer id);
    List<EnrollmentResponse> getEnrollmentsByLearner(Integer learnerId);
    List<EnrollmentResponse> getEnrollmentsByCourse(Integer courseId);
    EnrollmentResponse updateProgress(Integer enrollmentId, Integer moduleId, Integer percent, Integer timeSpentMinutes);
    EnrollmentResponse completeCourse(Integer enrollmentId);
    List<EnrollmentResponse> getAllEnrollments();

    // Sequential learning flow
    LearningStateResponse getLearningState(Integer enrollmentId);
    LearningStateResponse completeModule(Integer enrollmentId, Integer moduleId);
    LearningStateResponse refreshLearningState(Integer enrollmentId);
}