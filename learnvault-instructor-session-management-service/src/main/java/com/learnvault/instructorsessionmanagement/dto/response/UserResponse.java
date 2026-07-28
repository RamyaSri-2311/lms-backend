package com.learnvault.instructorsessionmanagement.dto.response;

import lombok.Data;

@Data
public class UserResponse {

    private Integer userId;
    private String name;
    private String role;
    private String email;
    private String phone;
    private String status;
}