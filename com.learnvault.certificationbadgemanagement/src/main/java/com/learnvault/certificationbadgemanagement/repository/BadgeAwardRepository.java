package com.learnvault.certificationbadgemanagement.repository;

import com.learnvault.certificationbadgemanagement.entity.BadgeAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeAwardRepository extends JpaRepository<BadgeAward, Integer> {
    List<BadgeAward> findByLearnerId(Integer learnerId);
    boolean existsByBadge_BadgeIdAndLearnerId(Integer badgeId, Integer learnerId);
}