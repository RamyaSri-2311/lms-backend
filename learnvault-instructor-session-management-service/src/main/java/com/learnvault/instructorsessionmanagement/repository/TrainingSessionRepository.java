package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    List<TrainingSession> findByInstructor_InstructorId(Integer instructorId);
    List<TrainingSession> findByCourseId(Integer courseId);
    List<TrainingSession> findByCourseIdIn(java.util.Collection<Integer> courseIds);
    List<TrainingSession> findByStatus(SessionStatus status);

    // Sessions for the same instructor on a given date, excluding a given status
    // (used to detect schedule time conflicts — we ignore CANCELLED sessions).
    List<TrainingSession> findByInstructor_InstructorIdAndSessionDateAndStatusNot(
            Integer instructorId, LocalDate sessionDate, SessionStatus status);
}