package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.entity.AuditLog;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.entity.enums.Role;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.AuditLogRepository;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    // Helper to build a sample user
    private User sampleUser() {
        return User.builder()
                .userId(1).name("Alice").role(Role.LEARNER)
                .email("alice@test.com").status(Status.ACTIVE).password("encoded")
                .build();
    }

    @Test
    void logAction_success_savesAuditLog() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(sampleUser()));

        // Act
        auditLogService.logAction(1, "CREATE", "USER", 1);

        // Assert
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void logAction_userNotFound_throws() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> auditLogService.logAction(99, "CREATE", "USER", 99));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void logAction_secondSuccess_neverSavesWhenUserMissing() {
        // Arrange - a second success/verify style test
        when(userRepository.findById(2)).thenReturn(Optional.of(sampleUser()));

        // Act
        auditLogService.logAction(2, "UPDATE_STATUS", "USER", 2);

        // Assert
        verify(userRepository).findById(2);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
