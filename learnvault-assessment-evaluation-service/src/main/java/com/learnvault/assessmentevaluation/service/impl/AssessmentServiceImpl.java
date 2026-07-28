package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.dto.request.AssessmentRequest;
import com.learnvault.assessmentevaluation.dto.response.AssessmentResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.enums.Status;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;

    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request) {
        log.info("Creating assessment for course: {}", request.getCourseId());

        Assessment assessment = Assessment.builder()
                .courseId(request.getCourseId())
                .moduleId(request.getModuleId())
                .type(request.getType())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .maxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 3)
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .status(Status.ACTIVE)
                .build();

        Assessment saved = assessmentRepository.save(assessment);
        return mapToResponse(saved);
    }

    @Override
    public AssessmentResponse getAssessmentById(Integer id) {
        log.info("Fetching assessment by ID: {}", id);
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        return mapToResponse(assessment);
    }

    @Override
    public List<AssessmentResponse> getAllAssessments(Integer courseId, Integer moduleId) {
        log.info("Fetching assessments - courseId: {}, moduleId: {}", courseId, moduleId);
        List<Assessment> assessments;
        if (courseId != null) {
            assessments = assessmentRepository.findByCourseId(courseId);
        } else if (moduleId != null) {
            assessments = assessmentRepository.findByModuleId(moduleId);
        } else {
            assessments = assessmentRepository.findAll();
        }
        return assessments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public AssessmentResponse updateStatus(Integer id, String status) {
        log.info("Updating assessment {} status to {}", id, status);
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        assessment.setStatus(Status.valueOf(status));
        Assessment updated = assessmentRepository.save(assessment);
        return mapToResponse(updated);
    }

    private AssessmentResponse mapToResponse(Assessment assessment) {
        return AssessmentResponse.builder()
                .assessmentId(assessment.getAssessmentId())
                .courseId(assessment.getCourseId())
                .moduleId(assessment.getModuleId())
                .type(assessment.getType())
                .totalMarks(assessment.getTotalMarks())
                .passingMarks(assessment.getPassingMarks())
                .maxAttempts(assessment.getMaxAttempts())
                .timeLimitMinutes(assessment.getTimeLimitMinutes())
                .status(assessment.getStatus())
                .build();
    }
}