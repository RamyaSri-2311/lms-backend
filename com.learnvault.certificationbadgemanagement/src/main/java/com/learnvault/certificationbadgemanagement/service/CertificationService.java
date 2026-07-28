package com.learnvault.certificationbadgemanagement.service;

import com.learnvault.certificationbadgemanagement.dto.request.CertificationRequest;
import com.learnvault.certificationbadgemanagement.dto.response.CertificationResponse;

import java.util.List;

public interface CertificationService {
    CertificationResponse issueCertification(CertificationRequest request);
    CertificationResponse getCertificationById(Integer id);
    List<CertificationResponse> getCertificationsByLearner(Integer learnerId);
    CertificationResponse revokeCertification(Integer id);
}