package com.learnvault.assessmentevaluation.service.impl;

import com.learnvault.assessmentevaluation.client.NotificationClient;
import com.learnvault.assessmentevaluation.client.dto.NotificationRequest;
import com.learnvault.assessmentevaluation.dto.request.AttemptRequest;
import com.learnvault.assessmentevaluation.dto.response.AttemptCountResponse;
import com.learnvault.assessmentevaluation.dto.response.AttemptResponse;
import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.AttemptRecord;
import com.learnvault.assessmentevaluation.entity.Question;
import com.learnvault.assessmentevaluation.exception.BadRequestException;
import com.learnvault.assessmentevaluation.exception.ResourceNotFoundException;
import com.learnvault.assessmentevaluation.repository.AssessmentRepository;
import com.learnvault.assessmentevaluation.repository.AttemptRepository;
import com.learnvault.assessmentevaluation.repository.QuestionRepository;
import com.learnvault.assessmentevaluation.service.AttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public AttemptResponse submitAttempt(AttemptRequest request) {
        log.info("Submitting attempt for assessment: {}, learner: {}", request.getAssessmentId(), request.getLearnerId());

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + request.getAssessmentId()));

        long attemptCount = attemptRepository.countByAssessment_AssessmentIdAndLearnerId(
                request.getAssessmentId(), request.getLearnerId());

        if (attemptCount >= assessment.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempts exceeded for this assessment");
        }

        // Authoritative server-side grading: compare submitted answers to the stored
        // correctAnswer with trimming + case-insensitive matching. Falls back to a
        // client-supplied score only when no answers are provided (legacy path).
        int score = gradeAttempt(assessment, request);

        boolean passed = score >= assessment.getPassingMarks();

        AttemptRecord attempt = AttemptRecord.builder()
                .assessment(assessment)
                .learnerId(request.getLearnerId())
                .attemptNumber((int) attemptCount + 1)
                .score(score)
                .passed(passed)
                .timeTakenMinutes(request.getTimeTakenMinutes())
                .build();

        AttemptRecord saved = attemptRepository.save(attempt);
        log.info("Attempt saved - Score: {}/{}, Passed: {}", saved.getScore(), assessment.getTotalMarks(), saved.getPassed());

        notifyResult(request.getLearnerId(), score, assessment, passed);

        return mapToResponse(saved);
    }

    private int gradeAttempt(Assessment assessment, AttemptRequest request) {
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return request.getScore() != null ? request.getScore() : 0;
        }
        List<Question> questions = questionRepository.findByAssessment_AssessmentId(assessment.getAssessmentId());
        int computed = 0;
        for (Question q : questions) {
            String submitted = request.getAnswers().get(q.getQuestionId());
            if (submitted != null && q.getCorrectAnswer() != null
                    && submitted.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                computed += (q.getMarks() != null ? q.getMarks() : 0);
            }
        }
        return computed;
    }

    private void notifyResult(Integer learnerId, int score, Assessment assessment, boolean passed) {
        if (learnerId == null) {
            return;
        }
        String message = passed
                ? "You have passed assessment #" + assessment.getAssessmentId()
                    + " with a score of " + score + "/" + assessment.getTotalMarks() + "."
                : "You did not meet the passing score for assessment #" + assessment.getAssessmentId()
                    + " (scored " + score + "/" + assessment.getTotalMarks() + "). Please retry.";
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .userId(learnerId)
                    .message(message)
                    .category("ASSESSMENT")
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to send assessment notification for learner {}: {}", learnerId, ex.getMessage());
        }
    }

    @Override
    public List<AttemptResponse> getAttemptHistory(Integer assessmentId, Integer learnerId) {
        log.info("Fetching attempt history - assessment: {}, learner: {}", assessmentId, learnerId);
        return attemptRepository.findByAssessment_AssessmentIdAndLearnerId(assessmentId, learnerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttemptCountResponse getAttemptCount(Integer assessmentId, Integer learnerId) {
        long count = attemptRepository.countByAssessment_AssessmentIdAndLearnerId(assessmentId, learnerId);
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + assessmentId));
        
        return AttemptCountResponse.builder()
                .attemptCount(count)
                .remaining(Math.max(0, assessment.getMaxAttempts() - (int) count))
                .build();
    }

    private AttemptResponse mapToResponse(AttemptRecord attempt) {
        return AttemptResponse.builder()
                .attemptId(attempt.getAttemptId())
                .assessmentId(attempt.getAssessment().getAssessmentId())
                .learnerId(attempt.getLearnerId())
                .attemptNumber(attempt.getAttemptNumber())
                .score(attempt.getScore())
                .passed(attempt.getPassed())
                .attemptDate(attempt.getAttemptDate())
                .timeTakenMinutes(attempt.getTimeTakenMinutes())
                .build();
    }

    @Override
    public List<AttemptResponse> getAllAttempts() {
        log.info("Fetching all attempts");
        return attemptRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}