package com.learnvault.learninganalyticsreporting.repository;

import com.learnvault.learninganalyticsreporting.entity.LearningReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningReportRepository extends JpaRepository<LearningReport, Integer> {
    List<LearningReport> findByScope(String scope);
}