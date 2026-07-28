package com.learnvault.identityaccessmanagement.service;

import com.learnvault.identityaccessmanagement.dto.request.UserRequest;
import com.learnvault.identityaccessmanagement.dto.response.UserResponse;
import com.learnvault.identityaccessmanagement.entity.enums.Status;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Integer id);
    List<UserResponse> getAllUsers();
    UserResponse updateUserStatus(Integer id, Status status);
    void deleteUser(Integer id);
}
