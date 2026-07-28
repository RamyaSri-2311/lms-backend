package com.learnvault.assessmentevaluation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptCountResponse {
    private Long attemptCount;
    private Integer remaining;
}