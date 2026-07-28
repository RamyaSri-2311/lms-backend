package com.learnvault.coursecatalogcontentmanagement.dto.response;

import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Integer courseId;
    private String title;
    private String category;
    private CourseLevel level;
    private Integer durationHours;
    private Integer instructorId;
    private DeliveryMode deliveryMode;
    private CourseStatus status;
}