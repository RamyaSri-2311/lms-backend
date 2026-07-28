package com.learnvault.coursecatalogcontentmanagement.dto.response;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponse {
    private Integer pathId;
    private String name;
    private String targetRole;
    private String courseSequence;
    private Integer totalHours;
    private LearningPathStatus status;
}