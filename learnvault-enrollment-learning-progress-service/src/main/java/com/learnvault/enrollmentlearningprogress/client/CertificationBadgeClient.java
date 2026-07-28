package com.learnvault.enrollmentlearningprogress.client;

import com.learnvault.enrollmentlearningprogress.dto.request.CertificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "certification-badge-management-service")
public interface CertificationBadgeClient {

    @PostMapping("/api/certifications")
    void issueCertificate(@RequestBody CertificationRequest request);
}