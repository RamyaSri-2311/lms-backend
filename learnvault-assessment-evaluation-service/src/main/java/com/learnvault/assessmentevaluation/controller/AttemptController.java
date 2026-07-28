package com.learnvault.assessmentevaluation.controller;

import com.learnvault.assessmentevaluation.dto.request.AttemptRequest;
import com.learnvault.assessmentevaluation.dto.response.AttemptCountResponse;
import com.learnvault.assessmentevaluation.dto.response.AttemptResponse;
import com.learnvault.assessmentevaluation.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    @PostMapping
    public ResponseEntity<AttemptResponse> submitAttempt(@RequestBody AttemptRequest request) {
        return new ResponseEntity<>(attemptService.submitAttempt(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AttemptResponse>> getAttempts(
            @RequestParam(required = false) Integer assessmentId,
            @RequestParam(required = false) Integer learnerId) {
        
        // If both provided, return history. Otherwise return all.
        if (assessmentId != null && learnerId != null) {
            return ResponseEntity.ok(attemptService.getAttemptHistory(assessmentId, learnerId));
        }
        return ResponseEntity.ok(attemptService.getAllAttempts());
    }

    @GetMapping("/{assessmentId}/learner/{learnerId}/count")
    public ResponseEntity<AttemptCountResponse> getAttemptCount(
            @PathVariable Integer assessmentId,
            @PathVariable Integer learnerId) {
        return ResponseEntity.ok(attemptService.getAttemptCount(assessmentId, learnerId));
    }
}