package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityResponse {
    private Integer maxCapacity;
    private Long registered;
    private Integer available;
}