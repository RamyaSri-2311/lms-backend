package com.learnvault.enrollmentlearningprogress.client.fallback;

import com.learnvault.enrollmentlearningprogress.client.CertificationBadgeClient;
import com.learnvault.enrollmentlearningprogress.dto.request.CertificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link CertificationBadgeClient}.
 * Used by the circuit breaker when the certification-badge-management-service is unavailable.
 */
@Slf4j
@Component
public class CertificationBadgeClientFallback implements CertificationBadgeClient {

    @Override
    public void issueCertificate(CertificationRequest request) {
        // certification-badge service is down: skip issuing instead of failing the request.
        log.warn("Fallback: certification-badge-management-service unavailable, skipping issueCertificate");
    }
}
