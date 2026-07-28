package com.learnvault.instructorsessionmanagement.dto.response;

import com.learnvault.instructorsessionmanagement.entity.enums.RegistrationSource;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorResponse {

    private String instructorName;   // resolved from Identity service
    private String email;            // resolved from Identity service
    private String phone;            // resolved from Identity service

    private Integer instructorId;
    private Integer userId;
    private String specializations;
    private String qualificationLevel;
    private String experience;
    private Double ratingAvg;
    private Status status;
    private RegistrationSource registrationSource;
    private LocalDateTime createdDate;
}
