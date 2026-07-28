package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;
import com.learnvault.instructorsessionmanagement.service.SessionRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/session-registrations")
@RequiredArgsConstructor
public class SessionRegistrationController {

    private final SessionRegistrationService sessionRegistrationService;

    @PostMapping
    public ResponseEntity<SessionRegistrationResponse> registerLearner(@RequestBody SessionRegistrationRequest request) {
        return new ResponseEntity<>(sessionRegistrationService.registerLearner(request), HttpStatus.CREATED);
    }

    // Registrations for an instructor's session, or a learner's own registrations
    @GetMapping
    public ResponseEntity<List<SessionRegistrationResponse>> getRegistrations(
            @RequestParam(required = false) Integer sessionId,
            @RequestParam(required = false) Integer learnerId) {
        if (learnerId != null) {
            return ResponseEntity.ok(sessionRegistrationService.getRegistrationsByLearner(learnerId));
        }
        return ResponseEntity.ok(sessionRegistrationService.getRegistrationsBySession(sessionId));
    }

    @PatchMapping("/{id}/attendance")
    public ResponseEntity<SessionRegistrationResponse> markAttendance(@PathVariable Integer id,
                                                                        @RequestParam String status) {
        return ResponseEntity.ok(sessionRegistrationService.markAttendance(id, status));
    }

    // Learner cancels their own registration (frees up capacity)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SessionRegistrationResponse> cancelRegistration(@PathVariable Integer id) {
        return ResponseEntity.ok(sessionRegistrationService.cancelRegistration(id));
    }
}