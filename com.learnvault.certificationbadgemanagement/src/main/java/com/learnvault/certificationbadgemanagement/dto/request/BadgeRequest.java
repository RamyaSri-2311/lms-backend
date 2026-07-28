package com.learnvault.certificationbadgemanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeRequest {
    private String name;
    private String criteria;
    private Integer courseId;
    private String imagePath;
}