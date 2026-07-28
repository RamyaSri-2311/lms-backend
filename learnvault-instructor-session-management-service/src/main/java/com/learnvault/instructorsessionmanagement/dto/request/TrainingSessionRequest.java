package com.learnvault.instructorsessionmanagement.dto.request;

import com.learnvault.instructorsessionmanagement.entity.enums.SessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionRequest {
    private Integer courseId;
    private Integer instructorId;
    private String title;
    private String description;
    private SessionType sessionType;   // ONLINE / OFFLINE
    private LocalDate sessionDate;
    private String startTime;          // "HH:mm" or "HH:mm:ss"
    private String endTime;            // "HH:mm" or "HH:mm:ss"
    private String venue;              // required for OFFLINE
    private String meetingLink;        // required for ONLINE
    private Integer maxCapacity;
}
