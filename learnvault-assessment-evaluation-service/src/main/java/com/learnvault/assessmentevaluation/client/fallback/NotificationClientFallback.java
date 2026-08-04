package com.learnvault.assessmentevaluation.client.fallback;

import com.learnvault.assessmentevaluation.client.NotificationClient;
import com.learnvault.assessmentevaluation.client.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link NotificationClient}. Used by Resilience4j when the
 * notification-alert-service is unavailable, so calls fail gracefully
 * instead of throwing.
 */
@Slf4j
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendNotification(NotificationRequest request) {
        log.warn("Fallback triggered: notification-alert-service is unavailable. "
                + "Skipping sendNotification for request: {}", request);
    }
}
