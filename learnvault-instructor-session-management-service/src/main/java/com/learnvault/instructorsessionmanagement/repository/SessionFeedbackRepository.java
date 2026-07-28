package com.learnvault.instructorsessionmanagement.repository;

import com.learnvault.instructorsessionmanagement.entity.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Integer> {
    List<SessionFeedback> findBySessionId(Integer sessionId);
    List<SessionFeedback> findByLearnerId(Integer learnerId);
    Optional<SessionFeedback> findBySessionIdAndLearnerId(Integer sessionId, Integer learnerId);
    boolean existsBySessionIdAndLearnerId(Integer sessionId, Integer learnerId);
    long countBySessionId(Integer sessionId);
}
