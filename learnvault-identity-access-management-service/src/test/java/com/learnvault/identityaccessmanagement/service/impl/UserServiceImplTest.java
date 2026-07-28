package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.dto.request.UserRequest;
import com.learnvault.identityaccessmanagement.dto.response.UserResponse;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.entity.enums.Role;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import com.learnvault.identityaccessmanagement.exception.DuplicateResourceException;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import com.learnvault.identityaccessmanagement.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserServiceImpl userService;

    // Helper to build a sample saved user
    private User sampleUser() {
        return User.builder()
                .userId(1)
                .name("Alice")
                .role(Role.LEARNER)
                .email("alice@test.com")
                .phone("12345")
                .status(Status.ACTIVE)
                .password("encoded")
                .build();
    }

    @Test
    void createUser_success() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .name("Alice").role(Role.LEARNER).email("alice@test.com")
                .phone("12345").password("raw").build();
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser());

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertEquals("alice@test.com", response.getEmail());
        assertEquals(Status.ACTIVE, response.getStatus());
        verify(userRepository).save(any(User.class));
        verify(auditLogService).logAction(1, "CREATE", "USER", 1);
    }

    @Test
    void getUserById_notFound_throws() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99));
    }

    @Test
    void createUser_duplicateEmail_throws() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .email("alice@test.com").build();
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
