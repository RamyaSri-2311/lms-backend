package com.learnvault.certificationbadgemanagement.dto.response;

import com.learnvault.certificationbadgemanagement.entity.enums.BadgeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {
    private Integer badgeId;
    private String name;
    private String criteria;
    private Integer courseId;
    private String imagePath;
    private BadgeStatus status;
}