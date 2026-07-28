package com.learnvault.assessmentevaluation.repository;

import com.learnvault.assessmentevaluation.entity.Assessment;
import com.learnvault.assessmentevaluation.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    List<Assessment> findByCourseId(Integer courseId);
    List<Assessment> findByModuleId(Integer moduleId);
    List<Assessment> findByStatus(Status status);
}