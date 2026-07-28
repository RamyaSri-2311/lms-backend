package com.learnvault.coursecatalogcontentmanagement.controller;

import com.learnvault.coursecatalogcontentmanagement.dto.request.LearningPathRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.LearningPathResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import com.learnvault.coursecatalogcontentmanagement.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @PostMapping
    public ResponseEntity<LearningPathResponse> createPath(@RequestBody LearningPathRequest request) {
        return new ResponseEntity<>(learningPathService.createPath(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LearningPathResponse>> getAllPaths() {
        return ResponseEntity.ok(learningPathService.getAllPaths());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningPathResponse> getPathById(@PathVariable Integer id) {
        return ResponseEntity.ok(learningPathService.getPathById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LearningPathResponse> updatePathStatus(@PathVariable Integer id,
                                                                  @RequestParam LearningPathStatus status) {
        return ResponseEntity.ok(learningPathService.updatePathStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePath(@PathVariable Integer id) {
        learningPathService.deletePath(id);
        return ResponseEntity.noContent().build();
    }
}