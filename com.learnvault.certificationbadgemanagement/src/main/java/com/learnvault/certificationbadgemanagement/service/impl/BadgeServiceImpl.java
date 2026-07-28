package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeResponse;
import com.learnvault.certificationbadgemanagement.entity.Badge;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.BadgeRepository;
import com.learnvault.certificationbadgemanagement.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;

    @Override
    public BadgeResponse createBadge(BadgeRequest request) {
        log.info("Creating badge: {}", request.getName());

        Badge badge = Badge.builder()
                .name(request.getName())
                .criteria(request.getCriteria())
                .courseId(request.getCourseId())
                .imagePath(request.getImagePath())
                .status(BadgeStatus.ACTIVE)
                .build();

        Badge saved = badgeRepository.save(badge);
        return mapToResponse(saved);
    }

    @Override
    public BadgeResponse getBadgeById(Integer id) {
        log.info("Fetching badge by ID: {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + id));
        return mapToResponse(badge);
    }

    @Override
    public List<BadgeResponse> getAllBadges() {
        log.info("Fetching all badges");
        return badgeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BadgeResponse mapToResponse(Badge badge) {
        return BadgeResponse.builder()
                .badgeId(badge.getBadgeId())
                .name(badge.getName())
                .criteria(badge.getCriteria())
                .courseId(badge.getCourseId())
                .imagePath(badge.getImagePath())
                .status(badge.getStatus())
                .build();
    }
}