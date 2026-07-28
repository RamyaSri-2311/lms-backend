package com.learnvault.certificationbadgemanagement.repository;

import com.learnvault.certificationbadgemanagement.entity.Badge;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findByStatus(BadgeStatus status);
    List<Badge> findByCourseId(Integer courseId);
    List<Badge> findByCourseIdAndStatus(Integer courseId, BadgeStatus status);
}