package com.learnvault.instructorsessionmanagement.dto.request;

import com.learnvault.instructorsessionmanagement.entity.enums.RegistrationSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorRequest {
    private Integer userId;
    private String specializations;
    private String qualificationLevel;
    private String experience;
    // Optional: how the profile was created. Defaults are applied server-side
    // (ADMIN_CREATED via POST /api/instructors, SELF_REGISTERED via POST /api/instructors/self).
    private RegistrationSource registrationSource;
}
