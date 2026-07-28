package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.SessionRegistration;
import com.learnvault.instructorsessionmanagement.entity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRegistrationRepository extends JpaRepository<SessionRegistration, Integer> {
    List<SessionRegistration> findBySession_SessionId(Integer sessionId);
    List<SessionRegistration> findByLearnerId(Integer learnerId);
    long countBySession_SessionId(Integer sessionId);

    Optional<SessionRegistration> findBySession_SessionIdAndLearnerId(Integer sessionId, Integer learnerId);
    long countBySession_SessionIdAndAttendanceStatusNot(Integer sessionId, AttendanceStatus status);
    long countBySession_SessionIdAndAttendanceStatus(Integer sessionId, AttendanceStatus status);
}
