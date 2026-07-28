package com.learnvault.certificationbadgemanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {
    private Integer courseId;
    private Integer learnerId;
    private LocalDate expiryDate;
}