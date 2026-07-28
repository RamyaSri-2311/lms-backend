package com.learnvault.identityaccessmanagement.dto.response;

import com.learnvault.identityaccessmanagement.entity.enums.Role;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String name;
    private Role role;
    private String email;
    private String phone;
    private Status status;
}
