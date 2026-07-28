package com.learnvault.coursecatalogcontentmanagement.client.dto;

import lombok.Data;

/**
 * Minimal view of an instructor as returned by the instructor-session-management-service.
 * Used only to validate that an assigned instructor exists and is active.
 */
@Data
public class InstructorDto {
    private Integer instructorId;
    private Integer userId;
    private String status; // ACTIVE / INACTIVE
}
