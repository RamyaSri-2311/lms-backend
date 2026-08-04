package com.learnvault.learninganalyticsreporting.client.fallback;

import com.learnvault.learninganalyticsreporting.client.IdentityAccessManagementClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link IdentityAccessManagementClient}.
 * Returned when the identity-access-management-service is unavailable
 * (circuit breaker open / call failure). Provides safe empty defaults.
 */
@Slf4j
@Component
public class IdentityAccessManagementClientFallback implements IdentityAccessManagementClient {

    @Override
    public List<Map<String, Object>> getAllUsers() {
        log.warn("Fallback: identity-access-management-service unavailable, returning empty list for getAllUsers()");
        return Collections.emptyList();
    }
}
