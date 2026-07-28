package com.learnvault.notalert.service;

import com.learnvault.notalert.dto.request.NotificationRequest;
import com.learnvault.notalert.dto.Response.NotificationResponse;
import com.learnvault.notalert.dto.Response.UnreadCountResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendNotification(NotificationRequest request);
    List<NotificationResponse> getByUser(Integer userId);
    List<NotificationResponse> getUnreadByUser(Integer userId);
    NotificationResponse markAsRead(Integer id);
    void markAllAsRead(Integer userId);
    UnreadCountResponse getUnreadCount(Integer userId);
}