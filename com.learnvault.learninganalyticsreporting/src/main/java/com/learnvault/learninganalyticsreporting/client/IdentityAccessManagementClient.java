package com.learnvault.learninganalyticsreporting.client;

import com.learnvault.learninganalyticsreporting.client.fallback.IdentityAccessManagementClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "identity-access-management-service", fallback = IdentityAccessManagementClientFallback.class)
public interface IdentityAccessManagementClient {

    @GetMapping("/api/users")
    List<Map<String, Object>> getAllUsers();
}