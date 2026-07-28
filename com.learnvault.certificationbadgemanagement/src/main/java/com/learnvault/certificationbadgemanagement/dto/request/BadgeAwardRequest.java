package com.learnvault.certificationbadgemanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAwardRequest {
    private Integer badgeId;
    private Integer learnerId;
}