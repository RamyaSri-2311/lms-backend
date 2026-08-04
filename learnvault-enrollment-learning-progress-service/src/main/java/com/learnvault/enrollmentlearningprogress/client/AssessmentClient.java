package com.learnvault.enrollmentlearningprogress.client;

import com.learnvault.enrollmentlearningprogress.client.dto.AssessmentDto;
import com.learnvault.enrollmentlearningprogress.client.dto.AttemptDto;
import com.learnvault.enrollmentlearningprogress.client.fallback.AssessmentClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "assessment-evaluation-service", fallback = AssessmentClientFallback.class)
public interface AssessmentClient {

    @GetMapping("/api/assessments")
    List<AssessmentDto> getAssessmentsByCourse(@RequestParam("courseId") Integer courseId);

    @GetMapping("/api/attempts")
    List<AttemptDto> getAttempts(@RequestParam("assessmentId") Integer assessmentId,
                                 @RequestParam("learnerId") Integer learnerId);
}
