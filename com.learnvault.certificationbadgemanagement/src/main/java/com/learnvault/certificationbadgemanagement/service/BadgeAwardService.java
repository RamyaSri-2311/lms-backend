package com.learnvault.certificationbadgemanagement.service;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeAwardRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeAwardResponse;

import java.util.List;

public interface BadgeAwardService {
    BadgeAwardResponse awardBadge(BadgeAwardRequest request);
    List<BadgeAwardResponse> getAwardsByLearner(Integer learnerId);
}