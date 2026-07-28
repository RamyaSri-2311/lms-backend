package com.learnvault.certificationbadgemanagement.service.impl;

import com.learnvault.certificationbadgemanagement.dto.request.BadgeRequest;
import com.learnvault.certificationbadgemanagement.dto.response.BadgeResponse;
import com.learnvault.certificationbadgemanagement.entity.Badge;
import com.learnvault.certificationbadgemanagement.entity.enums.BadgeStatus;
import com.learnvault.certificationbadgemanagement.exception.ResourceNotFoundException;
import com.learnvault.certificationbadgemanagement.repository.BadgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceImplTest {

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeServiceImpl badgeService;

    @Test
    void createBadge_success_savesAndReturnsBadge() {
        // Arrange
        BadgeRequest request = BadgeRequest.builder()
                .name("Java Master")
                .criteria("Complete Java course")
                .courseId(10)
                .imagePath("/img/java.png")
                .build();
        Badge saved = Badge.builder()
                .badgeId(1)
                .name("Java Master")
                .criteria("Complete Java course")
                .courseId(10)
                .imagePath("/img/java.png")
                .status(BadgeStatus.ACTIVE)
                .build();
        when(badgeRepository.save(any(Badge.class))).thenReturn(saved);

        // Act
        BadgeResponse response = badgeService.createBadge(request);

        // Assert
        assertEquals("Java Master", response.getName());
        assertEquals(BadgeStatus.ACTIVE, response.getStatus());
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void getBadgeById_notFound_throwsResourceNotFoundException() {
        // Arrange
        when(badgeRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> badgeService.getBadgeById(99));
    }

    @Test
    void getBadgeById_success_returnsBadge() {
        // Arrange
        Badge badge = Badge.builder()
                .badgeId(5)
                .name("Spring Pro")
                .status(BadgeStatus.ACTIVE)
                .build();
        when(badgeRepository.findById(5)).thenReturn(Optional.of(badge));

        // Act
        BadgeResponse response = badgeService.getBadgeById(5);

        // Assert
        assertEquals(5, response.getBadgeId());
        assertEquals("Spring Pro", response.getName());
    }
}
