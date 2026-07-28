package com.learnvault.coursecatalogcontentmanagement.dto.response;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse {
    private Integer moduleId;
    private Integer courseId;
    private String title;
    private Integer sequenceOrder;
    private ContentType contentType;
    private String contentURL;
    private Integer durationMinutes;
}