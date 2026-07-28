package com.learnvault.instructorsessionmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRegistrationRequest {
    private Integer sessionId;
    private Integer learnerId;
}