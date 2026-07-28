package com.learnvault.enrollmentlearningprogress.repository;

import com.learnvault.enrollmentlearningprogress.entity.Enrollment;
import com.learnvault.enrollmentlearningprogress.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByLearnerId(Integer learnerId);
    List<Enrollment> findByCourseId(Integer courseId);
    List<Enrollment> findByStatus(EnrollmentStatus status);
    Optional<Enrollment> findByLearnerIdAndCourseId(Integer learnerId, Integer courseId);
}