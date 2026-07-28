package com.learnvault.assessmentevaluation.controller;

import com.learnvault.assessmentevaluation.dto.request.AssessmentRequest;
import com.learnvault.assessmentevaluation.dto.response.AssessmentResponse;
import com.learnvault.assessmentevaluation.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    public ResponseEntity<AssessmentResponse> createAssessment(@RequestBody AssessmentRequest request) {
        return new ResponseEntity<>(assessmentService.createAssessment(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> getAllAssessments(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer moduleId) {
        return ResponseEntity.ok(assessmentService.getAllAssessments(courseId, moduleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponse> getAssessmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AssessmentResponse> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return ResponseEntity.ok(assessmentService.updateStatus(id, status));
    }
}