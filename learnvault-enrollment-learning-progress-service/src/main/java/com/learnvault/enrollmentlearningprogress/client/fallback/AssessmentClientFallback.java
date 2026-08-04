package com.learnvault.enrollmentlearningprogress.client.fallback;

import com.learnvault.enrollmentlearningprogress.client.AssessmentClient;
import com.learnvault.enrollmentlearningprogress.client.dto.AssessmentDto;
import com.learnvault.enrollmentlearningprogress.client.dto.AttemptDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback for {@link AssessmentClient}.
 * Used by the circuit breaker when the assessment-evaluation-service is unavailable.
 */
@Slf4j
@Component
public class AssessmentClientFallback implements AssessmentClient {

    @Override
    public List<AssessmentDto> getAssessmentsByCourse(Integer courseId) {
        // assessment service is down: return an empty assessment list.
        log.warn("Fallback: assessment-evaluation-service unavailable, returning empty assessments for courseId={}", courseId);
        return Collections.emptyList();
    }

    @Override
    public List<AttemptDto> getAttempts(Integer assessmentId, Integer learnerId) {
        // assessment service is down: return an empty attempt list.
        log.warn("Fallback: assessment-evaluation-service unavailable, returning empty attempts for assessmentId={}, learnerId={}", assessmentId, learnerId);
        return Collections.emptyList();
    }
}
