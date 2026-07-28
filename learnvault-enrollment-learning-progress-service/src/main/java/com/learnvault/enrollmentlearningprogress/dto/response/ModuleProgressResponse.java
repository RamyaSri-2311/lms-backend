package com.learnvault.enrollmentlearningprogress.dto.response;

import com.learnvault.enrollmentlearningprogress.entity.enums.ModuleProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressResponse {
    private Integer progressId;
    private Integer enrollmentId;
    private Integer moduleId;
    private LocalDate startedDate;
    private LocalDate completedDate;
    private Integer timeSpentMinutes;
    private ModuleProgressStatus status;
}