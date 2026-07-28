package com.learnvault.notalert.service.impl;

import com.learnvault.notalert.dto.Response.NotificationResponse;
import com.learnvault.notalert.dto.request.NotificationRequest;
import com.learnvault.notalert.entities.Notification;
import com.learnvault.notalert.entity.enums.NotificationCategory;
import com.learnvault.notalert.entity.enums.NotificationStatus;
import com.learnvault.notalert.exception.ResourceNotFoundException;
import com.learnvault.notalert.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    // 1) Success case: sendNotification saves and returns mapped response
    @Test
    void sendNotification_success() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .userId(1)
                .message("Welcome")
                .category(NotificationCategory.ENROLLMENT)
                .build();
        Notification saved = Notification.builder()
                .notificationId(10)
                .userId(1)
                .message("Welcome")
                .category(NotificationCategory.ENROLLMENT)
                .status(NotificationStatus.UNREAD)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        // Act
        NotificationResponse response = notificationService.sendNotification(request);

        // Assert
        assertEquals(10, response.getNotificationId());
        assertEquals("Welcome", response.getMessage());
        assertEquals(NotificationStatus.UNREAD, response.getStatus());
        verify(notificationRepository).save(any(Notification.class));
    }

    // 2) Not found case: markAsRead when notification is missing
    @Test
    void markAsRead_notFound() {
        // Arrange
        when(notificationRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // 3) Second success case: markAsRead updates status to READ and saves
    @Test
    void markAsRead_success() {
        // Arrange
        Notification existing = Notification.builder()
                .notificationId(5)
                .userId(1)
                .message("Hello")
                .category(NotificationCategory.SESSION)
                .status(NotificationStatus.UNREAD)
                .build();
        when(notificationRepository.findById(5)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenReturn(existing);

        // Act
        NotificationResponse response = notificationService.markAsRead(5);

        // Assert
        assertEquals(NotificationStatus.READ, response.getStatus());
        verify(notificationRepository).save(existing);
    }
}
