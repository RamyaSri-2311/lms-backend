package com.learnvault.instructorsessionmanagement.client.fallback;

import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link UserClient}. Returns safe defaults when the
 * identity-access-management-service is unavailable.
 */
@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Integer id) {
        log.warn("UserClient fallback: getUserById({}) - service unavailable, returning null", id);
        return null;
    }
}
