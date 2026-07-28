package com.learnvault.assessmentevaluation.repository;

import com.learnvault.assessmentevaluation.entity.AttemptRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<AttemptRecord, Integer> {
    List<AttemptRecord> findByAssessment_AssessmentIdAndLearnerId(Integer assessmentId, Integer learnerId);
    long countByAssessment_AssessmentIdAndLearnerId(Integer assessmentId, Integer learnerId);
}