package com.learnvault.instructorsessionmanagement.client.fallback;

import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link NotificationClient}. Silently drops the notification when the
 * notification-alert-service is unavailable.
 */
@Slf4j
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendNotification(NotificationRequest request) {
        log.warn("NotificationClient fallback: sendNotification(...) - service unavailable, notification dropped");
    }
}
