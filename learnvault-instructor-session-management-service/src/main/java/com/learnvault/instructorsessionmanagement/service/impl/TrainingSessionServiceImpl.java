package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.CourseClient;
import com.learnvault.instructorsessionmanagement.client.EnrollmentClient;
import com.learnvault.instructorsessionmanagement.client.NotificationClient;
import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.client.dto.CourseDto;
import com.learnvault.instructorsessionmanagement.client.dto.EnrollmentDto;
import com.learnvault.instructorsessionmanagement.client.dto.NotificationRequest;
import com.learnvault.instructorsessionmanagement.dto.request.TrainingSessionRequest;
import com.learnvault.instructorsessionmanagement.dto.response.CapacityResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.LearnerSessionStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.SessionAnalyticsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.TrainingSessionResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.SessionFeedback;
import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionType;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import com.learnvault.instructorsessionmanagement.repository.SessionFeedbackRepository;
import com.learnvault.instructorsessionmanagement.repository.SessionRegistrationRepository;
import com.learnvault.instructorsessionmanagement.repository.TrainingSessionRepository;
import com.learnvault.instructorsessionmanagement.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final InstructorRepository instructorRepository;
    private final SessionRegistrationRepository sessionRegistrationRepository;
    private final SessionFeedbackRepository sessionFeedbackRepository;
    private final CourseClient courseClient;
    private final EnrollmentClient enrollmentClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public TrainingSessionResponse scheduleSession(TrainingSessionRequest request) {
        log.info("Scheduling session for course {} by instructor {}", request.getCourseId(), request.getInstructorId());

        Instructor instructor = instructorRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor not found with id: " + request.getInstructorId()));

        // Validate the instructor is assigned to the course
        CourseDto course = fetchCourse(request.getCourseId());
        if (course == null) {
            throw new BadRequestException("Course not found with id: " + request.getCourseId());
        }
        if (course.getInstructorId() == null || !course.getInstructorId().equals(request.getInstructorId())) {
            throw new BadRequestException("You can only create sessions for courses assigned to you.");
        }

        // Session-type specific validation
        if (request.getSessionType() == null) {
            throw new BadRequestException("Session type (ONLINE/OFFLINE) is required.");
        }
        if (request.getSessionType() == SessionType.ONLINE
                && (request.getMeetingLink() == null || request.getMeetingLink().isBlank())) {
            throw new BadRequestException("Meeting link is mandatory for online sessions.");
        }
        if (request.getSessionType() == SessionType.OFFLINE
                && (request.getVenue() == null || request.getVenue().isBlank())) {
            throw new BadRequestException("Venue is mandatory for offline sessions.");
        }

        Time start = parseTime(request.getStartTime());
        Time end = parseTime(request.getEndTime());
        if (!end.after(start)) {
            throw new BadRequestException("End time must be greater than start time.");
        }

        TrainingSession session = TrainingSession.builder()
                .courseId(request.getCourseId())
                .instructor(instructor)
                .title(request.getTitle())
                .description(request.getDescription())
                .sessionType(request.getSessionType())
                .sessionDate(request.getSessionDate())
                .startTime(start)
                .endTime(end)
                .venue(request.getVenue())
                .meetingLink(request.getMeetingLink())
                .maxCapacity(request.getMaxCapacity())
                .status(SessionStatus.SCHEDULED)
                .build();

        TrainingSession saved = trainingSessionRepository.save(session);

        // Notify enrolled learners that a new session is available
        notifyEnrolledLearners(saved,
                "New session \"" + safe(saved.getTitle()) + "\" scheduled for "
                        + (course.getTitle() != null ? course.getTitle() : ("course " + saved.getCourseId()))
                        + " on " + saved.getSessionDate() + ".");

        return mapToResponse(saved, null, new HashMap<>(), new HashMap<>());
    }

    @Override
    public TrainingSessionResponse getSessionById(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        return mapToResponse(session, null, new HashMap<>(), new HashMap<>());
    }

    @Override
    public List<TrainingSessionResponse> getAllSessions(Integer courseId, Integer instructorId) {
        List<TrainingSession> sessions;
        if (courseId != null) {
            sessions = trainingSessionRepository.findByCourseId(courseId);
        } else if (instructorId != null) {
            sessions = trainingSessionRepository.findByInstructor_InstructorId(instructorId);
        } else {
            sessions = trainingSessionRepository.findAll();
        }
        Map<Integer, String> courseNames = new HashMap<>();
        Map<Integer, String> userNames = new HashMap<>();
        return sessions.stream()
                .map(s -> mapToResponse(s, null, courseNames, userNames))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingSessionResponse> getSessionsForLearner(Integer learnerId) {
        List<Integer> courseIds = new ArrayList<>();
        try {
            List<EnrollmentDto> enrollments = enrollmentClient.getByLearner(learnerId);
            if (enrollments != null) {
                courseIds = enrollments.stream()
                        .map(EnrollmentDto::getCourseId)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            log.warn("Could not fetch enrollments for learner {}: {}", learnerId, ex.getMessage());
        }
        if (courseIds.isEmpty()) {
            return List.of();
        }
        List<TrainingSession> sessions = trainingSessionRepository.findByCourseIdIn(courseIds);
        Map<Integer, String> courseNames = new HashMap<>();
        Map<Integer, String> userNames = new HashMap<>();
        return sessions.stream()
                .map(s -> mapToResponse(s, learnerId, courseNames, userNames))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrainingSessionResponse startSession(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED sessions can be started.");
        }
        session.setStatus(SessionStatus.IN_PROGRESS);
        TrainingSession updated = trainingSessionRepository.save(session);
        return mapToResponse(updated, null, new HashMap<>(), new HashMap<>());
    }

    @Override
    @Transactional
    public TrainingSessionResponse completeSession(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        if (session.getStatus() == SessionStatus.COMPLETED || session.getStatus() == SessionStatus.CANCELLED) {
            throw new BadRequestException("Session is already " + session.getStatus() + ".");
        }
        session.setStatus(SessionStatus.COMPLETED);
        TrainingSession updated = trainingSessionRepository.save(session);

        // Ask registered learners for feedback
        notifyActiveRegistrants(updated,
                "Session \"" + safe(updated.getTitle()) + "\" has been completed. Please submit your feedback.");

        return mapToResponse(updated, null, new HashMap<>(), new HashMap<>());
    }

    @Override
    @Transactional
    public TrainingSessionResponse cancelSession(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        if (session.getStatus() == SessionStatus.COMPLETED || session.getStatus() == SessionStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel a session that is already " + session.getStatus() + ".");
        }
        session.setStatus(SessionStatus.CANCELLED);
        TrainingSession updated = trainingSessionRepository.save(session);

        notifyActiveRegistrants(updated,
                "Session \"" + safe(updated.getTitle()) + "\" scheduled on " + updated.getSessionDate() + " has been cancelled.");

        return mapToResponse(updated, null, new HashMap<>(), new HashMap<>());
    }

    @Override
    public CapacityResponse getCapacity(Integer id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + id));
        long registered = activeCount(id);
        return CapacityResponse.builder()
                .maxCapacity(session.getMaxCapacity())
                .registered(registered)
                .available(session.getMaxCapacity() - (int) registered)
                .build();
    }

    @Override
    public SessionAnalyticsResponse getAnalytics(Integer sessionId) {
        trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found with id: " + sessionId));

        long registrationCount = sessionRegistrationRepository
                .countBySession_SessionIdAndAttendanceStatusNot(sessionId, AttendanceStatus.CANCELLED);
        long presentCount = sessionRegistrationRepository
                .countBySession_SessionIdAndAttendanceStatus(sessionId, AttendanceStatus.ATTENDED);
        long absentCount = sessionRegistrationRepository
                .countBySession_SessionIdAndAttendanceStatus(sessionId, AttendanceStatus.ABSENT);
        double attendancePct = registrationCount > 0
                ? Math.round((presentCount * 10000.0) / registrationCount) / 100.0
                : 0.0;

        List<SessionFeedback> feedback = sessionFeedbackRepository.findBySessionId(sessionId);
        long feedbackCount = feedback.size();
        double avgRating = feedbackCount > 0
                ? Math.round((feedback.stream().mapToInt(f -> f.getRating() != null ? f.getRating() : 0).sum() * 100.0)
                    / feedbackCount) / 100.0
                : 0.0;

        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int r = i;
            distribution.put(r, feedback.stream().filter(f -> f.getRating() != null && f.getRating() == r).count());
        }

        return SessionAnalyticsResponse.builder()
                .sessionId(sessionId)
                .registrationCount(registrationCount)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .attendancePercentage(attendancePct)
                .averageRating(avgRating)
                .feedbackCount(feedbackCount)
                .ratingDistribution(distribution)
                .build();
    }

    @Override
    public InstructorSessionStatsResponse getInstructorStats(Integer instructorId) {
        List<TrainingSession> sessions = trainingSessionRepository.findByInstructor_InstructorId(instructorId);
        long total = sessions.size();
        long upcoming = sessions.stream()
                .filter(s -> s.getStatus() == SessionStatus.SCHEDULED || s.getStatus() == SessionStatus.IN_PROGRESS)
                .count();
        long completed = sessions.stream().filter(s -> s.getStatus() == SessionStatus.COMPLETED).count();

        long registeredLearners = 0;
        long totalPresent = 0;
        long totalAbsent = 0;
        long ratingSum = 0;
        long ratingCount = 0;
        for (TrainingSession s : sessions) {
            registeredLearners += sessionRegistrationRepository
                    .countBySession_SessionIdAndAttendanceStatusNot(s.getSessionId(), AttendanceStatus.CANCELLED);
            totalPresent += sessionRegistrationRepository
                    .countBySession_SessionIdAndAttendanceStatus(s.getSessionId(), AttendanceStatus.ATTENDED);
            totalAbsent += sessionRegistrationRepository
                    .countBySession_SessionIdAndAttendanceStatus(s.getSessionId(), AttendanceStatus.ABSENT);
            for (SessionFeedback f : sessionFeedbackRepository.findBySessionId(s.getSessionId())) {
                if (f.getRating() != null) {
                    ratingSum += f.getRating();
                    ratingCount++;
                }
            }
        }
        // Attendance % is measured over sessions where attendance was actually taken.
        long totalAttendanceBase = totalPresent + totalAbsent;
        double attendancePct = totalAttendanceBase > 0
                ? Math.round((totalPresent * 10000.0) / totalAttendanceBase) / 100.0 : 0.0;
        double avgRating = ratingCount > 0
                ? Math.round((ratingSum * 100.0) / ratingCount) / 100.0 : 0.0;

        return InstructorSessionStatsResponse.builder()
                .totalSessions(total)
                .upcomingSessions(upcoming)
                .completedSessions(completed)
                .registeredLearners(registeredLearners)
                .attendancePercentage(attendancePct)
                .averageRating(avgRating)
                .build();
    }

    @Override
    public LearnerSessionStatsResponse getLearnerStats(Integer learnerId) {
        List<SessionRegistration> regs = sessionRegistrationRepository.findByLearnerId(learnerId).stream()
                .filter(r -> r.getAttendanceStatus() != AttendanceStatus.CANCELLED)
                .collect(Collectors.toList());

        long registered = regs.size();
        long upcoming = 0;
        long completed = 0;
        long pendingFeedback = 0;
        long attended = 0;
        long absent = 0;

        for (SessionRegistration r : regs) {
            TrainingSession s = r.getSession();
            if (s == null) continue;
            SessionStatus st = s.getStatus();
            AttendanceStatus a = r.getAttendanceStatus();

            if (st == SessionStatus.SCHEDULED || st == SessionStatus.IN_PROGRESS) {
                upcoming++;
            }
            if (st == SessionStatus.COMPLETED) {
                completed++;
                if (!sessionFeedbackRepository.existsBySessionIdAndLearnerId(s.getSessionId(), learnerId)) {
                    pendingFeedback++;
                }
            }

            // Attendance reflects the actual marked record, independent of session status,
            // so the percentage updates as soon as an instructor marks Present/Absent.
            if (a == AttendanceStatus.ATTENDED) {
                attended++;
            } else if (a == AttendanceStatus.ABSENT) {
                absent++;
            }
        }
        long attendanceBase = attended + absent;
        double attendancePct = attendanceBase > 0
                ? Math.round((attended * 10000.0) / attendanceBase) / 100.0 : 0.0;

        return LearnerSessionStatsResponse.builder()
                .upcomingSessions(upcoming)
                .registeredSessions(registered)
                .completedSessions(completed)
                .pendingFeedback(pendingFeedback)
                .attendancePercentage(attendancePct)
                .build();
    }

    // ---------------- helpers ----------------

    private long activeCount(Integer sessionId) {
        return sessionRegistrationRepository
                .countBySession_SessionIdAndAttendanceStatusNot(sessionId, AttendanceStatus.CANCELLED);
    }

    private CourseDto fetchCourse(Integer courseId) {
        try {
            return courseClient.getCourseById(courseId);
        } catch (Exception ex) {
            log.warn("Could not fetch course {}: {}", courseId, ex.getMessage());
            return null;
        }
    }

    private void notifyEnrolledLearners(TrainingSession session, String message) {
        try {
            List<EnrollmentDto> enrollments = enrollmentClient.getByCourse(session.getCourseId());
            if (enrollments != null) {
                for (EnrollmentDto e : enrollments) {
                    sendNotification(e.getLearnerId(), message);
                }
            }
        } catch (Exception ex) {
            log.warn("Could not notify enrolled learners for session {}: {}", session.getSessionId(), ex.getMessage());
        }
    }

    private void notifyActiveRegistrants(TrainingSession session, String message) {
        for (SessionRegistration r : sessionRegistrationRepository.findBySession_SessionId(session.getSessionId())) {
            if (r.getAttendanceStatus() != AttendanceStatus.CANCELLED) {
                sendNotification(r.getLearnerId(), message);
            }
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

    private TrainingSessionResponse mapToResponse(TrainingSession session, Integer learnerId,
                                                  Map<Integer, String> courseNames,
                                                  Map<Integer, String> userNames) {
        long registered = activeCount(session.getSessionId());

        String courseName = courseNames.computeIfAbsent(session.getCourseId(), cid -> {
            CourseDto c = fetchCourse(cid);
            return c != null ? c.getTitle() : null;
        });

        Integer instructorUserId = session.getInstructor() != null ? session.getInstructor().getUserId() : null;
        String instructorName = instructorUserId == null ? null
                : userNames.computeIfAbsent(instructorUserId, uid -> {
                    try {
                        var u = userClient.getUserById(uid);
                        return u != null ? u.getName() : null;
                    } catch (Exception ex) {
                        return null;
                    }
                });

        String myStatus = null;
        Integer myRegId = null;
        if (learnerId != null) {
            var reg = sessionRegistrationRepository
                    .findBySession_SessionIdAndLearnerId(session.getSessionId(), learnerId);
            if (reg.isPresent()) {
                myStatus = reg.get().getAttendanceStatus().name();
                myRegId = reg.get().getRegistrationId();
            }
        }

        return TrainingSessionResponse.builder()
                .sessionId(session.getSessionId())
                .courseId(session.getCourseId())
                .courseName(courseName)
                .instructorId(session.getInstructor() != null ? session.getInstructor().getInstructorId() : null)
                .instructorName(instructorName)
                .title(session.getTitle())
                .description(session.getDescription())
                .sessionType(session.getSessionType())
                .sessionDate(session.getSessionDate())
                .startTime(fmtTime(session.getStartTime()))
                .endTime(fmtTime(session.getEndTime()))
                .venue(session.getVenue())
                .meetingLink(session.getMeetingLink())
                .maxCapacity(session.getMaxCapacity())
                .registeredCount(registered)
                .availableSeats(session.getMaxCapacity() != null ? session.getMaxCapacity() - (int) registered : 0)
                .status(session.getStatus())
                .myRegistrationStatus(myStatus)
                .myRegistrationId(myRegId)
                .build();
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }

    static Time parseTime(String s) {
        if (s == null || s.isBlank()) {
            throw new BadRequestException("Start and end time are required.");
        }
        String v = s.trim();
        if (v.length() == 5) {
            v = v + ":00"; // HH:mm -> HH:mm:ss
        }
        try {
            return Time.valueOf(v);
        } catch (Exception e) {
            throw new BadRequestException("Invalid time value: " + s);
        }
    }

    static String fmtTime(Time t) {
        if (t == null) return null;
        String s = t.toString();
        return s.length() >= 5 ? s.substring(0, 5) : s;
    }
}
