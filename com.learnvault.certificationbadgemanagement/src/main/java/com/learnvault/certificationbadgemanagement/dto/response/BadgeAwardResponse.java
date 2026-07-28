package com.learnvault.certificationbadgemanagement.dto.response;

import com.learnvault.certificationbadgemanagement.entity.enums.BadgeAwardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAwardResponse {
    private Integer awardId;
    private Integer badgeId;
    private String badgeName;
    private Integer learnerId;
    private LocalDate awardedDate;
    private BadgeAwardStatus status;
}