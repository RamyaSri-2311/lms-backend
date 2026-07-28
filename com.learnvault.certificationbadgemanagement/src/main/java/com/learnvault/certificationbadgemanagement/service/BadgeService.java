package com.learnvault.certificationbadgemanagement.service;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeResponse;

import java.util.List;

public interface BadgeService {
    BadgeResponse createBadge(BadgeRequest request);
    BadgeResponse getBadgeById(Integer id);
    List<BadgeResponse> getAllBadges();
}