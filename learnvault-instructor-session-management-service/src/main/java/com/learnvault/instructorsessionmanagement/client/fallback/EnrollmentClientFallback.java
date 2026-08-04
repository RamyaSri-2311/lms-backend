package com.learnvault.instructorsessionmanagement.client.fallback;

import com.learnvault.instructorsessionmanagement.client.EnrollmentClient;
import com.learnvault.instructorsessionmanagement.client.dto.EnrollmentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback for {@link EnrollmentClient}. Returns safe defaults when the
 * enrollment-learning-progress-service is unavailable.
 */
@Slf4j
@Component
public class EnrollmentClientFallback implements EnrollmentClient {

    @Override
    public List<EnrollmentDto> getByLearner(Integer learnerId) {
        log.warn("EnrollmentClient fallback: getByLearner({}) - service unavailable, returning empty list", learnerId);
        return Collections.emptyList();
    }

    @Override
    public List<EnrollmentDto> getByCourse(Integer courseId) {
        log.warn("EnrollmentClient fallback: getByCourse({}) - service unavailable, returning empty list", courseId);
        return Collections.emptyList();
    }
}
