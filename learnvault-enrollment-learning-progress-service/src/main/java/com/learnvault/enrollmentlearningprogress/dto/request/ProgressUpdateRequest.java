package com.learnvault.enrollmentlearningprogress.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {
    private Integer moduleId;
    private Integer percent;
    private Integer timeSpentMinutes;
}