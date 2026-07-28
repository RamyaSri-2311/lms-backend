package com.learnvault.coursecatalogcontentmanagement.controller;

import com.learnvault.coursecatalogcontentmanagement.dto.request.ModuleRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.ModuleResponse;
import com.learnvault.coursecatalogcontentmanagement.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponse> addModule(@PathVariable Integer courseId,
                                                    @RequestBody ModuleRequest request) {
        return new ResponseEntity<>(moduleService.addModule(courseId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> getModulesByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleResponse> getModuleById(@PathVariable Integer moduleId) {
        return ResponseEntity.ok(moduleService.getModuleById(moduleId));
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<ModuleResponse> updateModule(@PathVariable Integer moduleId,
                                                         @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(moduleService.updateModule(moduleId, request));
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Integer moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }
}