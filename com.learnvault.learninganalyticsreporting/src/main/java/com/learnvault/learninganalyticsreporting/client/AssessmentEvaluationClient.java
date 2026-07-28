package com.learnvault.learninganalyticsreporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "assessment-evaluation-service")
public interface AssessmentEvaluationClient {

    @GetMapping("/api/assessments")
    List<Map<String, Object>> getAllAssessments();

    @GetMapping("/api/attempts")
    List<Map<String, Object>> getAllAttempts();
}