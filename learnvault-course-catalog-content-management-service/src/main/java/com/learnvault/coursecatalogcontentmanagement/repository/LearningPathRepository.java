package com.learnvault.coursecatalogcontentmanagement.repository;

import com.learnvault.coursecatalogcontentmanagement.entity.LearningPath;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Integer> {
    List<LearningPath> findByStatus(LearningPathStatus status);
    List<LearningPath> findByTargetRole(String targetRole);
}