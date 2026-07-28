package com.learnvault.identityaccessmanagement.dto.response;

import com.learnvault.identityaccessmanagement.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Integer userId;   // ← ADD THIS
    private String email;
    private String name;
    private Role role;
}