package com.learnvault.certificationbadgemanagement.repository;

import com.learnvault.certificationbadgemanagement.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    List<Certification> findByLearnerId(Integer learnerId);
    Optional<Certification> findByCourseIdAndLearnerId(Integer courseId, Integer learnerId);
}