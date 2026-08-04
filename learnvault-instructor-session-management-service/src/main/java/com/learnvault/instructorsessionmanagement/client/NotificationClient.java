package com.learnvault.instructorsessionmanagement.client;

import com.learnvault.instructorsessionmanagement.client.dto.NotificationRequest;
import com.learnvault.instructorsessionmanagement.client.fallback.NotificationClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-alert-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void sendNotification(@RequestBody NotificationRequest request);
}
