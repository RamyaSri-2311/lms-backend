package com.learnvault.learninganalyticsreporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "enrollment-learning-progress-service")
public interface EnrollmentLearningProgressClient {

    @GetMapping("/api/enrollments")
    List<Map<String, Object>> getAllEnrollments();
}