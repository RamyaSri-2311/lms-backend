package com.learnvault.instructorsessionmanagement.controller;

import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorStatsResponse;
import com.learnvault.instructorsessionmanagement.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    // Admin-created instructor profile
    @PostMapping
    public ResponseEntity<InstructorResponse> registerInstructor(@RequestBody InstructorRequest request) {
        return new ResponseEntity<>(instructorService.registerInstructor(request), HttpStatus.CREATED);
    }

    // Instructor self-registers / completes their own profile
    @PostMapping("/self")
    public ResponseEntity<InstructorResponse> selfRegisterInstructor(@RequestBody InstructorRequest request) {
        return new ResponseEntity<>(instructorService.selfRegisterInstructor(request), HttpStatus.CREATED);
    }

    // List instructors; optional ?status=ACTIVE|INACTIVE filter (used by the course-creation dropdown)
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(instructorService.getInstructorsByStatus(status));
        }
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }

    // Convenience endpoint for active instructors
    @GetMapping("/active")
    public ResponseEntity<List<InstructorResponse>> getActiveInstructors() {
        return ResponseEntity.ok(instructorService.getInstructorsByStatus("ACTIVE"));
    }

    // Aggregate counts for the admin dashboard
    @GetMapping("/stats")
    public ResponseEntity<InstructorStatsResponse> getStats() {
        return ResponseEntity.ok(instructorService.getInstructorStats());
    }

    // Resolve the instructor profile for a given user (used by the instructor dashboard)
    @GetMapping("/user/{userId}")
    public ResponseEntity<InstructorResponse> getInstructorByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(instructorService.getInstructorByUserId(userId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Integer id) {
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<InstructorResponse> updateRating(@PathVariable Integer id,
                                                           @RequestParam Double value) {
        return ResponseEntity.ok(instructorService.updateRating(id, value));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InstructorResponse> updateStatus(@PathVariable Integer id,
                                                           @RequestParam String status) {
        return ResponseEntity.ok(instructorService.updateStatus(id, status));
    }
}
