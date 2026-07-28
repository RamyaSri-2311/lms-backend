package com.learnvault.instructorsessionmanagement.dto.response;

import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRegistrationResponse {
    private Integer registrationId;
    private Integer sessionId;
    private Integer learnerId;
    private String learnerName;
    private LocalDate registeredDate;
    private AttendanceStatus attendanceStatus;
}