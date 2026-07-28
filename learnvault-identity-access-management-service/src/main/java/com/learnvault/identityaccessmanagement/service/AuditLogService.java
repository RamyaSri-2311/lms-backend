package com.learnvault.identityaccessmanagement.service;

public interface AuditLogService {
    void logAction(Integer userId, String action, String entityType, Integer entityId);
}
