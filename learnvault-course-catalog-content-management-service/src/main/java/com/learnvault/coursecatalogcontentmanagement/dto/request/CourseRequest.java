package com.learnvault.coursecatalogcontentmanagement.dto.request;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private String title;
    private String category;
    private CourseLevel level;
    private Integer durationHours;
    private Integer instructorId;
    private DeliveryMode deliveryMode;
}