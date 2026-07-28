package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.TrainingSession;
import com.learnvault.instructorsessionmanagement.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    List<TrainingSession> findByInstructor_InstructorId(Integer instructorId);
    List<TrainingSession> findByCourseId(Integer courseId);
    List<TrainingSession> findByCourseIdIn(java.util.Collection<Integer> courseIds);
    List<TrainingSession> findByStatus(SessionStatus status);
}