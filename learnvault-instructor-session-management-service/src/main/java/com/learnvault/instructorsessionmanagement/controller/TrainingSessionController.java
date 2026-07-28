package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.LearnerSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.SessionAnalyticsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;
import com.learnvault.instructorsessionmanagement.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> scheduleSession(@RequestBody TrainingSessionRequest request) {
        return new ResponseEntity<>(trainingSessionService.scheduleSession(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionResponse>> getAllSessions(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer instructorId) {
        return ResponseEntity.ok(trainingSessionService.getAllSessions(courseId, instructorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionResponse> getSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.getSessionById(id));
    }

    // Sessions visible to a learner (only for courses the learner is enrolled in)
    @GetMapping("/learner/{learnerId}")
    public ResponseEntity<List<TrainingSessionResponse>> getSessionsForLearner(@PathVariable Integer learnerId) {
        return ResponseEntity.ok(trainingSessionService.getSessionsForLearner(learnerId));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<TrainingSessionResponse> startSession(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.startSession(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TrainingSessionResponse> completeSession(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.completeSession(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TrainingSessionResponse> cancelSession(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.cancelSession(id));
    }

    @GetMapping("/{id}/capacity")
    public ResponseEntity<CapacityResponse> getCapacity(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.getCapacity(id));
    }

    // Feedback + attendance analytics for a single session
    @GetMapping("/{id}/analytics")
    public ResponseEntity<SessionAnalyticsResponse> getAnalytics(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingSessionService.getAnalytics(id));
    }

    // Instructor dashboard aggregate stats
    @GetMapping("/instructor/{instructorId}/stats")
    public ResponseEntity<InstructorSessionStatsResponse> getInstructorStats(@PathVariable Integer instructorId) {
        return ResponseEntity.ok(trainingSessionService.getInstructorStats(instructorId));
    }

    // Learner dashboard aggregate stats
    @GetMapping("/learner/{learnerId}/stats")
    public ResponseEntity<LearnerSessionStatsResponse> getLearnerStats(@PathVariable Integer learnerId) {
        return ResponseEntity.ok(trainingSessionService.getLearnerStats(learnerId));
    }
}