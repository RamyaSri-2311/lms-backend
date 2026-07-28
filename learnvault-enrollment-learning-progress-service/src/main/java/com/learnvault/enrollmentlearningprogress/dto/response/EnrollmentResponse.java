package com.learnvault.enrollmentlearningprogress.dto.response;

import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Integer enrollmentId;
    private Integer learnerId;
    private Integer courseId;
    private LocalDate enrollmentDate;
    private LocalDate deadlineDate;
    private LocalDate completionDate;
    private Integer progressPercent;
    private EnrollmentStatus status;
}