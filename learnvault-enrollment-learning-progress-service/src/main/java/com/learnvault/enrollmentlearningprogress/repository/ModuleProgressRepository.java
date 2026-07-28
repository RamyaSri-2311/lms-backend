package com.learnvault.enrollmentlearningprogress.repository;

import com.learnvault.enrollmentlearningprogress.entity.ModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Integer> {
    List<ModuleProgress> findByEnrollment_EnrollmentId(Integer enrollmentId);
    Optional<ModuleProgress> findByEnrollment_EnrollmentIdAndModuleId(Integer enrollmentId, Integer moduleId);
}