package com.learnvault.learninganalyticsreporting.client.fallback;

import com.learnvault.learninganalyticsreporting.client.AssessmentEvaluationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link AssessmentEvaluationClient}.
 * Returned when the assessment-evaluation-service is unavailable
 * (circuit breaker open / call failure). Provides safe empty defaults.
 */
@Slf4j
@Component
public class AssessmentEvaluationClientFallback implements AssessmentEvaluationClient {

    @Override
    public List<Map<String, Object>> getAllAssessments() {
        log.warn("Fallback: assessment-evaluation-service unavailable, returning empty list for getAllAssessments()");
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getAllAttempts() {
        log.warn("Fallback: assessment-evaluation-service unavailable, returning empty list for getAllAttempts()");
        return Collections.emptyList();
    }
}
