package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.client.dto.NotificationRequest;
import com.learnvault.instructorsessionmanagement.dto.request.SessionFeedbackRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionFeedbackResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.SessionFeedback;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.SessionFeedbackRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import com.learnvault.instructorsessionmanagement.service.SessionFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionFeedbackServiceImpl implements SessionFeedbackService {

    private final SessionFeedbackRepository sessionFeedbackRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public SessionFeedbackResponse submitFeedback(SessionFeedbackRequest request) {
        log.info("Submitting feedback for session {} by learner {}", request.getSessionId(), request.getLearnerId());

        if (request.getSessionId() == null || request.getLearnerId() == null) {
            throw new BadRequestException("Session id and learner id are required.");
        }

        TrainingSession session = trainingSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + request.getSessionId()));

        // Feedback is only allowed once the session has been completed
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new BadRequestException("Feedback can only be submitted after the session is completed.");
        }

        validateRating("Rating", request.getRating(), true);
        validateRating("Session quality", request.getSessionQuality(), false);
        validateRating("Instructor knowledge", request.getInstructorKnowledge(), false);
        validateRating("Content relevance", request.getContentRelevance(), false);

        // One feedback submission per learner per session
        if (sessionFeedbackRepository.existsBySessionIdAndLearnerId(request.getSessionId(), request.getLearnerId())) {
            throw new BadRequestException("You have already submitted feedback for this session.");
        }

        SessionFeedback feedback = SessionFeedback.builder()
                .sessionId(request.getSessionId())
                .learnerId(request.getLearnerId())
                .rating(request.getRating())
                .sessionQuality(request.getSessionQuality())
                .instructorKnowledge(request.getInstructorKnowledge())
                .contentRelevance(request.getContentRelevance())
                .comments(request.getComments())
                .build();

        SessionFeedback saved = sessionFeedbackRepository.save(feedback);

        notifyInstructor(session, request.getRating());

        return mapToResponse(saved, new HashMap<>());
    }

    @Override
    public List<SessionFeedbackResponse> getFeedbackBySession(Integer sessionId) {
        Map<Integer, String> nameCache = new HashMap<>();
        return sessionFeedbackRepository.findBySessionId(sessionId).stream()
                .map(f -> mapToResponse(f, nameCache))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionFeedbackResponse> getFeedbackByLearner(Integer learnerId) {
        Map<Integer, String> nameCache = new HashMap<>();
        return sessionFeedbackRepository.findByLearnerId(learnerId).stream()
                .map(f -> mapToResponse(f, nameCache))
                .collect(Collectors.toList());
    }

    @Override
    public SessionFeedbackResponse getLearnerFeedbackForSession(Integer sessionId, Integer learnerId) {
        return sessionFeedbackRepository.findBySessionIdAndLearnerId(sessionId, learnerId)
                .map(f -> mapToResponse(f, new HashMap<>()))
                .orElse(null);
    }

    // ---------------- helpers ----------------

    private void validateRating(String label, Integer value, boolean required) {
        if (value == null) {
            if (required) {
                throw new BadRequestException(label + " is required.");
            }
            return;
        }
        if (value < 1 || value > 5) {
            throw new BadRequestException(label + " must be between 1 and 5.");
        }
    }

    private void notifyInstructor(TrainingSession session, Integer rating) {
        Instructor instructor = session.getInstructor();
        if (instructor == null || instructor.getUserId() == null) {
            return;
        }
        String message = "New feedback (" + rating + "★) received for session \""
                + safe(session.getTitle()) + "\".";
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .userId(instructor.getUserId())
                    .message(message)
                    .category("SESSION")
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to notify instructor {} of new feedback: {}",
                    instructor.getUserId(), ex.getMessage());
        }
    }

    private String resolveName(Integer learnerId, Map<Integer, String> cache) {
        if (learnerId == null) return null;
        return cache.computeIfAbsent(learnerId, id -> {
            try {
                var u = userClient.getUserById(id);
                return u != null ? u.getName() : null;
            } catch (Exception ex) {
                return null;
            }
        });
    }

    private SessionFeedbackResponse mapToResponse(SessionFeedback f, Map<Integer, String> nameCache) {
        return SessionFeedbackResponse.builder()
                .feedbackId(f.getFeedbackId())
                .sessionId(f.getSessionId())
                .learnerId(f.getLearnerId())
                .learnerName(resolveName(f.getLearnerId(), nameCache))
                .rating(f.getRating())
                .sessionQuality(f.getSessionQuality())
                .instructorKnowledge(f.getInstructorKnowledge())
                .contentRelevance(f.getContentRelevance())
                .comments(f.getComments())
                .submittedDate(f.getSubmittedDate())
                .build();
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
