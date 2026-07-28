package com.learnvault.instructorsessionmanagement.dto.response;

import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
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
public class TrainingSessionResponse {
    private Integer sessionId;
    private Integer courseId;
    private String courseName;
    private Integer instructorId;
    private String instructorName;
    private String title;
    private String description;
    private SessionType sessionType;
    private LocalDate sessionDate;
    private String startTime;   // "HH:mm"
    private String endTime;     // "HH:mm"
    private String venue;
    private String meetingLink;
    private Integer maxCapacity;
    private long registeredCount;
    private int availableSeats;
    private SessionStatus status;

    // For learner "My Sessions": this learner's registration state for the session
    private String myRegistrationStatus;   // REGISTERED / ATTENDED / ABSENT / CANCELLED / null
    private Integer myRegistrationId;
}
