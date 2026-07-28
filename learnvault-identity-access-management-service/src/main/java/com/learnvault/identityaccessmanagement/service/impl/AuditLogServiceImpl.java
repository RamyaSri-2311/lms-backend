
package com.learnvault.identityaccessmanagement.service.impl;

import com.learnvault.identityaccessmanagement.entity.AuditLog;
import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.exception.ResourceNotFoundException;
import com.learnvault.identityaccessmanagement.repository.AuditLogRepository;
import com.learnvault.identityaccessmanagement.repository.UserRepository;
import com.learnvault.identityaccessmanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    public void logAction(Integer userId, String action, String entityType, Integer entityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        auditLogRepository.save(auditLog);
        log.debug("Audit logged: {} on {} by user {}", action, entityType, userId);
    }
}
