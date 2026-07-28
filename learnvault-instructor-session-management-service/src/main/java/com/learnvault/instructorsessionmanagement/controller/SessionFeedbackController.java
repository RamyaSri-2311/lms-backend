package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.SessionFeedbackRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionFeedbackResponse;
import com.learnvault.instructorsessionmanagement.service.SessionFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session-feedback")
@RequiredArgsConstructor
public class SessionFeedbackController {

    private final SessionFeedbackService sessionFeedbackService;

    // Learner submits feedback (only after the session is completed, once per session)
    @PostMapping
    public ResponseEntity<SessionFeedbackResponse> submitFeedback(@RequestBody SessionFeedbackRequest request) {
        return new ResponseEntity<>(sessionFeedbackService.submitFeedback(request), HttpStatus.CREATED);
    }

    // Feedback for a session (instructor "View Feedback"), or a learner's own submissions
    @GetMapping
    public ResponseEntity<List<SessionFeedbackResponse>> getFeedback(
            @RequestParam(required = false) Integer sessionId,
            @RequestParam(required = false) Integer learnerId) {
        if (learnerId != null && sessionId == null) {
            return ResponseEntity.ok(sessionFeedbackService.getFeedbackByLearner(learnerId));
        }
        if (sessionId != null && learnerId != null) {
            SessionFeedbackResponse mine = sessionFeedbackService.getLearnerFeedbackForSession(sessionId, learnerId);
            return ResponseEntity.ok(mine == null ? List.of() : List.of(mine));
        }
        return ResponseEntity.ok(sessionFeedbackService.getFeedbackBySession(sessionId));
    }
}
