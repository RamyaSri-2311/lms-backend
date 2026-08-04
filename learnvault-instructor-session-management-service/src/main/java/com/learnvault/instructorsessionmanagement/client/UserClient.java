package com.learnvault.instructorsessionmanagement.client;

import com.learnvault.instructorsessionmanagement.dto.response.UserResponse;
import com.learnvault.instructorsessionmanagement.client.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-access-management-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable Integer id);
}