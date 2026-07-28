package com.learnvault.enrollmentlearningprogress.client.dto;

import lombok.Data;

/** Module as returned by the course-catalog service (subset). */
@Data
public class ModuleDto {
    private Integer moduleId;
    private String title;
    private Integer sequenceOrder;
    private String contentType;
    private String contentURL;
    private Integer durationMinutes;
}
