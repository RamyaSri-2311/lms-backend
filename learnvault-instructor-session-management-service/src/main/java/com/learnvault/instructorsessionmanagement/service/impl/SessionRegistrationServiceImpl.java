package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.EnrollmentClient;
import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.client.dto.EnrollmentDto;
import com.learnvault.instructorsessionmanagement.client.dto.NotificationRequest;
import com.learnvault.instructorsessionmanagement.dto.request.SessionRegistrationRequest;
import com.learnvault.instructorsessionmanagement.dto.response.SessionRegistrationResponse;
import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import com.learnvault.instructorsessionmanagement.service.SessionRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionRegistrationServiceImpl implements SessionRegistrationService {

    private final SessionRegistrationRepository sessionRegistrationRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final EnrollmentClient enrollmentClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public SessionRegistrationResponse registerLearner(SessionRegistrationRequest request) {
        log.info("Registering learner {} for session {}", request.getLearnerId(), request.getSessionId());

        TrainingSession session = trainingSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));

        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new BadRequestException("Registration is only open for scheduled sessions.");
        }

        // Learner must be enrolled in the session's course
        if (!isLearnerEnrolled(request.getLearnerId(), session.getCourseId())) {
            throw new BadRequestException("You must be enrolled in the course to register for its sessions.");
        }

        long activeCount = sessionRegistrationRepository
                .countBySession_SessionIdAndAttendanceStatusNot(session.getSessionId(), AttendanceStatus.CANCELLED);

        Optional<SessionRegistration> existing = sessionRegistrationRepository
                .findBySession_SessionIdAndLearnerId(session.getSessionId(), request.getLearnerId());

        SessionRegistration registration;
        if (existing.isPresent()) {
            registration = existing.get();
            if (registration.getAttendanceStatus() != AttendanceStatus.CANCELLED) {
                throw new BadRequestException("You are already registered for this session.");
            }
            // Re-registering after a previous cancellation
            if (activeCount >= session.getMaxCapacity()) {
                throw new BadRequestException("Session is full. Max capacity: " + session.getMaxCapacity());
            }
            registration.setAttendanceStatus(AttendanceStatus.REGISTERED);
        } else {
            if (activeCount >= session.getMaxCapacity()) {
                throw new BadRequestException("Session is full. Max capacity: " + session.getMaxCapacity());
            }
            registration = SessionRegistration.builder()
                    .session(session)
                    .learnerId(request.getLearnerId())
                    .attendanceStatus(AttendanceStatus.REGISTERED)
                    .build();
        }

        SessionRegistration saved = sessionRegistrationRepository.save(registration);

        sendNotification(request.getLearnerId(),
                "You have successfully registered for session \"" + safe(session.getTitle()) + "\" on "
                        + session.getSessionDate() + ".");

        return mapToResponse(saved, new HashMap<>());
    }

    @Override
    public List<SessionRegistrationResponse> getRegistrationsBySession(Integer sessionId) {
        Map<Integer, String> nameCache = new HashMap<>();
        return sessionRegistrationRepository.findBySession_SessionId(sessionId)
                .stream()
                .map(r -> mapToResponse(r, nameCache))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionRegistrationResponse> getRegistrationsByLearner(Integer learnerId) {
        Map<Integer, String> nameCache = new HashMap<>();
        return sessionRegistrationRepository.findByLearnerId(learnerId)
                .stream()
                .map(r -> mapToResponse(r, nameCache))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SessionRegistrationResponse cancelRegistration(Integer id) {
        SessionRegistration registration = sessionRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
        registration.setAttendanceStatus(AttendanceStatus.CANCELLED);
        SessionRegistration updated = sessionRegistrationRepository.save(registration);
        return mapToResponse(updated, new HashMap<>());
    }

    @Override
    @Transactional
    public SessionRegistrationResponse markAttendance(Integer id, String status) {
        log.info("Marking attendance {} for registration {}", status, id);
        SessionRegistration registration = sessionRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
        registration.setAttendanceStatus(parseAttendance(status));
        SessionRegistration updated = sessionRegistrationRepository.save(registration);
        return mapToResponse(updated, new HashMap<>());
    }

    // ---------------- helpers ----------------

    private AttendanceStatus parseAttendance(String status) {
        if (status == null) {
            throw new BadRequestException("Attendance status is required.");
        }
        String s = status.trim().toUpperCase();
        if (s.equals("PRESENT")) {
            return AttendanceStatus.ATTENDED;
        }
        try {
            return AttendanceStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid attendance status: " + status);
        }
    }

    private boolean isLearnerEnrolled(Integer learnerId, Integer courseId) {
        try {
            List<EnrollmentDto> enrollments = enrollmentClient.getByLearner(learnerId);
            if (enrollments == null) return false;
            return enrollments.stream().anyMatch(e -> courseId != null && courseId.equals(e.getCourseId()));
        } catch (Exception ex) {
            log.warn("Could not verify enrollment for learner {} course {}: {}", learnerId, courseId, ex.getMessage());
            return false;
        }
    }

    private void sendNotification(Integer userId, String message) {
        if (userId == null) return;
        try {
            notificationClient.sendNotification(NotificationRequest.builder()
                    .userId(userId).message(message).category("SESSION").build());
        } catch (Exception ex) {
            log.warn("Failed to send SESSION notification to {}: {}", userId, ex.getMessage());
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

    private SessionRegistrationResponse mapToResponse(SessionRegistration registration, Map<Integer, String> nameCache) {
        return SessionRegistrationResponse.builder()
                .registrationId(registration.getRegistrationId())
                .sessionId(registration.getSession().getSessionId())
                .learnerId(registration.getLearnerId())
                .learnerName(resolveName(registration.getLearnerId(), nameCache))
                .registeredDate(registration.getRegisteredDate())
                .attendanceStatus(registration.getAttendanceStatus())
                .build();
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
