package com.learnvault.identityaccessmanagement.controller;

import com.learnvault.identityaccessmanagement.entity.AuditLog;
import com.learnvault.identityaccessmanagement.repository.AuditLogRepository;
import com.learnvault.identityaccessmanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;

    @PostMapping
    public ResponseEntity<Void> logAction(@RequestParam Integer userId,
                                          @RequestParam String action,
                                          @RequestParam String entityType,
                                          @RequestParam(required = false) Integer entityId) {
        auditLogService.logAction(userId, action, entityType, entityId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(Pageable pageable,
                                                          @RequestParam(required = false) Integer userId,
                                                          @RequestParam(required = false) String action,
                                                          @RequestParam(required = false) LocalDateTime from,
                                                          @RequestParam(required = false) LocalDateTime to) {
        if (userId != null) {
            List<AuditLog> logs = auditLogRepository.findByUser_UserId(userId);
            // For simplicity returning all; in production use PageImpl
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(auditLogRepository.findAll(pageable));
    }
}
