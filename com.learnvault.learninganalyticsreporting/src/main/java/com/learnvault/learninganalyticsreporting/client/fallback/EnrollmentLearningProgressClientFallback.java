package com.learnvault.learninganalyticsreporting.client.fallback;

import com.learnvault.learninganalyticsreporting.client.EnrollmentLearningProgressClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link EnrollmentLearningProgressClient}.
 * Returned when the enrollment-learning-progress-service is unavailable
 * (circuit breaker open / call failure). Provides safe empty defaults.
 */
@Slf4j
@Component
public class EnrollmentLearningProgressClientFallback implements EnrollmentLearningProgressClient {

    @Override
    public List<Map<String, Object>> getAllEnrollments() {
        log.warn("Fallback: enrollment-learning-progress-service unavailable, returning empty list for getAllEnrollments()");
        return Collections.emptyList();
    }
}
