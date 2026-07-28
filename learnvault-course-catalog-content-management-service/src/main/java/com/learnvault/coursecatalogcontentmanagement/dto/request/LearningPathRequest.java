package com.learnvault.coursecatalogcontentmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathRequest {
    private String name;
    private String targetRole;
    private String courseSequence;
    private Integer totalHours;
}