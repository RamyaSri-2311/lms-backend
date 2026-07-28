package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.dto.request.LearningPathRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.LearningPathResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.LearningPath;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.LearningPathRepository;
import com.learnvault.coursecatalogcontentmanagement.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements LearningPathService {

    private final LearningPathRepository learningPathRepository;

    @Override
    public LearningPathResponse createPath(LearningPathRequest request) {
        log.info("Creating learning path: {}", request.getName());

        LearningPath path = LearningPath.builder()
                .name(request.getName())
                .targetRole(request.getTargetRole())
                .courseSequence(request.getCourseSequence())
                .totalHours(request.getTotalHours())
                .status(LearningPathStatus.ACTIVE)
                .build();

        LearningPath saved = learningPathRepository.save(path);
        return mapToResponse(saved);
    }

    @Override
    public LearningPathResponse getPathById(Integer id) {
        log.info("Fetching learning path by ID: {}", id);
        LearningPath path = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning path not found with id: " + id));
        return mapToResponse(path);
    }

    @Override
    public List<LearningPathResponse> getAllPaths() {
        log.info("Fetching all learning paths");
        return learningPathRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LearningPathResponse updatePathStatus(Integer id, LearningPathStatus status) {
        log.info("Updating learning path {} status to {}", id, status);
        LearningPath path = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning path not found with id: " + id));
        path.setStatus(status);
        LearningPath updated = learningPathRepository.save(path);
        return mapToResponse(updated);
    }

    @Override
    public void deletePath(Integer id) {
        log.info("Deleting learning path: {}", id);
        LearningPath path = learningPathRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning path not found with id: " + id));
        learningPathRepository.delete(path);
    }

    private LearningPathResponse mapToResponse(LearningPath path) {
        return LearningPathResponse.builder()
                .pathId(path.getPathId())
                .name(path.getName())
                .targetRole(path.getTargetRole())
                .courseSequence(path.getCourseSequence())
                .totalHours(path.getTotalHours())
                .status(path.getStatus())
                .build();
    }
}