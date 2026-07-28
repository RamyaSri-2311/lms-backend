package com.learnvault.enrollmentlearningprogress.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    private Integer learnerId;
    private Integer courseId;
    private LocalDate deadlineDate;
}