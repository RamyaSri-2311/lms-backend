package com.learnvault.enrollmentlearningprogress.controller;

import com.learnvault.enrollmentlearningprogress.dto.response.ModuleProgressResponse;
import com.learnvault.enrollmentlearningprogress.service.ModuleProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments/{enrollmentId}/progress")
@RequiredArgsConstructor
public class ModuleProgressController {

    private final ModuleProgressService moduleProgressService;

    @GetMapping
    public ResponseEntity<List<ModuleProgressResponse>> getProgressByEnrollment(
            @PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(moduleProgressService.getProgressByEnrollment(enrollmentId));
    }

    @PatchMapping("/{moduleId}")
    public ResponseEntity<ModuleProgressResponse> updateModuleStatus(
            @PathVariable Integer enrollmentId,
            @PathVariable Integer moduleId,
            @RequestParam String status,
            @RequestParam(required = false) Integer timeSpentMinutes) {
        return ResponseEntity.ok(moduleProgressService.updateModuleStatus(
                enrollmentId, moduleId, status, timeSpentMinutes));
    }
}