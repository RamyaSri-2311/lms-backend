package com.learnvault.enrollmentlearningprogress.client;

import com.learnvault.enrollmentlearningprogress.client.fallback.CertificationBadgeClientFallback;
import com.learnvault.enrollmentlearningprogress.dto.request.CertificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "certification-badge-management-service", fallback = CertificationBadgeClientFallback.class)
public interface CertificationBadgeClient {

    @PostMapping("/api/certifications")
    void issueCertificate(@RequestBody CertificationRequest request);
}