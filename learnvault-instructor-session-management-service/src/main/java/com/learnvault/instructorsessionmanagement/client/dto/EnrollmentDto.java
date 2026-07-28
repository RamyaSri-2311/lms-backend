package com.learnvault.instructorsessionmanagement.client.dto;

import lombok.Data;

/** Enrollment as returned by the enrollment service (subset). */
@Data
public class EnrollmentDto {
    private Integer enrollmentId;
    private Integer learnerId;
    private Integer courseId;
    private String status;
}
