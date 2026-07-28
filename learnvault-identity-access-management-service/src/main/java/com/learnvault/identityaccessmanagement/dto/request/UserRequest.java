package com.learnvault.identityaccessmanagement.dto.request;

import com.learnvault.identityaccessmanagement.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String name;
    private Role role;
    private String email;
    private String phone;
    private String password;
}
