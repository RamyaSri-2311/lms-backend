package com.learnvault.certificationbadgemanagement.dto.response;

import com.learnvault.certificationbadgemanagement.entity.enums.CertificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponse {
    private Integer certificationId;
    private Integer courseId;
    private Integer learnerId;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String certificateNumber;
    private CertificationStatus status;
}