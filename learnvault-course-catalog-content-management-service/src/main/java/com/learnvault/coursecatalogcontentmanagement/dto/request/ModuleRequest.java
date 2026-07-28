package com.learnvault.coursecatalogcontentmanagement.dto.request;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRequest {
    private String title;
    private Integer sequenceOrder;
    private ContentType contentType;
    private String contentURL;
    private Integer durationMinutes;
}