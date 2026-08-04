package com.learnvault.enrollmentlearningprogress.client.fallback;

import com.learnvault.enrollmentlearningprogress.client.NotificationAlertClient;
import com.learnvault.enrollmentlearningprogress.dto.request.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link NotificationAlertClient}.
 * Used by the circuit breaker when the notification-alert-service is unavailable.
 */
@Slf4j
@Component
public class NotificationAlertClientFallback implements NotificationAlertClient {

    @Override
    public void sendNotification(NotificationRequest request) {
        // notification-alert-service is down: skip sending instead of failing the request.
        log.warn("Fallback: notification-alert-service unavailable, skipping sendNotification");
    }
}
