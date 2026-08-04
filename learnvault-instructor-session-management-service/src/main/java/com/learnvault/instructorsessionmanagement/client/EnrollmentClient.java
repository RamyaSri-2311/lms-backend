package com.learnvault.instructorsessionmanagement.client;

import com.learnvault.instructorsessionmanagement.client.dto.EnrollmentDto;
import com.learnvault.instructorsessionmanagement.client.fallback.EnrollmentClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "enrollment-learning-progress-service", fallback = EnrollmentClientFallback.class)
public interface EnrollmentClient {

    @GetMapping("/api/enrollments")
    List<EnrollmentDto> getByLearner(@RequestParam("learnerId") Integer learnerId);

    @GetMapping("/api/enrollments")
    List<EnrollmentDto> getByCourse(@RequestParam("courseId") Integer courseId);
}
