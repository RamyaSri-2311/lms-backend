package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.dto.request.UserRequest;
import com.learnvault.identityaccessmanagement.dto.response.UserResponse;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import com.learnvault.identityaccessmanagement.exception.DuplicateResourceException;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import com.learnvault.identityaccessmanagement.service.AuditLogService;
import com.learnvault.identityaccessmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(Status.ACTIVE)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);
        auditLogService.logAction(saved.getUserId(), "CREATE", "USER", saved.getUserId());

        log.info("User created with ID: {}", saved.getUserId());
        return mapToResponse(saved);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUserStatus(Integer id, Status status) {
        log.info("Updating user {} status to {}", id, status);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(status);
        User updated = userRepository.save(user);
        auditLogService.logAction(updated.getUserId(), "UPDATE_STATUS", "USER", updated.getUserId());
        return mapToResponse(updated);
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Soft-deleting user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        auditLogService.logAction(user.getUserId(), "DELETE", "USER", user.getUserId());
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .build();
    }
}
