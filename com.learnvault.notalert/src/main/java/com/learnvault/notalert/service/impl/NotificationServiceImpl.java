package com.learnvault.notalert.service.impl;

import com.learnvault.notalert.dto.request.NotificationRequest;
import com.learnvault.notalert.dto.Response.NotificationResponse;
import com.learnvault.notalert.dto.Response.UnreadCountResponse;
import com.learnvault.notalert.entities.Notification;
import com.learnvault.notalert.entity.enums.NotificationStatus;
import com.learnvault.notalert.exception.ResourceNotFoundException;
import com.learnvault.notalert.repository.NotificationRepository;
import com.learnvault.notalert.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending notification to user {}: {}", request.getUserId(), request.getMessage());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .message(request.getMessage())
                .category(request.getCategory())
                .status(NotificationStatus.UNREAD)
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    public List<NotificationResponse> getByUser(Integer userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadByUser(Integer userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Integer id) {
        log.info("Marking notification {} as read", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setStatus(NotificationStatus.READ);
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Override
    public void markAllAsRead(Integer userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        List<Notification> unread = notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        unread.forEach(n -> n.setStatus(NotificationStatus.READ));
        notificationRepository.saveAll(unread);
    }

    @Override
    public UnreadCountResponse getUnreadCount(Integer userId) {
        long count = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        return UnreadCountResponse.builder().count(count).build();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .category(notification.getCategory())
                .status(notification.getStatus())
                .createdDate(notification.getCreatedDate())
                .build();
    }
}