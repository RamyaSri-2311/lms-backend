package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeAwardRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeAwardResponse;
import com.learnvault.certificationbadgemanagement.entity.Badge;
import com.learnvault.certificationbadgemanagement.entity.BadgeAward;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeAwardStatus;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.BadgeAwardRepository;
import com.learnvault.certificationbadgemanagement.repository.BadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeAwardServiceImplTest {

    @Mock
    private BadgeAwardRepository badgeAwardRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeAwardServiceImpl badgeAwardService;

    @Test
    void awardBadge_success_savesAndReturnsAward() {
        // Arrange
        BadgeAwardRequest request = BadgeAwardRequest.builder()
                .badgeId(1)
                .learnerId(100)
                .build();
        Badge badge = Badge.builder()
                .badgeId(1)
                .name("Java Master")
                .status(BadgeStatus.ACTIVE)
                .build();
        BadgeAward saved = BadgeAward.builder()
                .awardId(50)
                .badge(badge)
                .learnerId(100)
                .status(BadgeAwardStatus.ACTIVE)
                .build();
        when(badgeRepository.findById(1)).thenReturn(Optional.of(badge));
        when(badgeAwardRepository.save(any(BadgeAward.class))).thenReturn(saved);

        // Act
        BadgeAwardResponse response = badgeAwardService.awardBadge(request);

        // Assert
        assertEquals(50, response.getAwardId());
        assertEquals("Java Master", response.getBadgeName());
        assertEquals(100, response.getLearnerId());
        verify(badgeAwardRepository).save(any(BadgeAward.class));
    }

    @Test
    void awardBadge_badgeNotFound_throwsResourceNotFoundException() {
        // Arrange
        BadgeAwardRequest request = BadgeAwardRequest.builder()
                .badgeId(99)
                .learnerId(100)
                .build();
        when(badgeRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> badgeAwardService.awardBadge(request));
    }

    @Test
    void getAwardsByLearner_success_returnsAwards() {
        // Arrange
        Badge badge = Badge.builder().badgeId(1).name("Java Master").build();
        BadgeAward award = BadgeAward.builder()
                .awardId(50)
                .badge(badge)
                .learnerId(100)
                .status(BadgeAwardStatus.ACTIVE)
                .build();
        when(badgeAwardRepository.findByLearnerId(100)).thenReturn(List.of(award));

        // Act
        List<BadgeAwardResponse> responses = badgeAwardService.getAwardsByLearner(100);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Java Master", responses.get(0).getBadgeName());
    }
}
