package com.learnvault.instructorsessionmanagement.client.dto;

import lombok.Data;

/** Course as returned by the course-catalog service (subset). */
@Data
public class CourseDto {
    private Integer courseId;
    private String title;
    private Integer instructorId;
    private String status;
}
