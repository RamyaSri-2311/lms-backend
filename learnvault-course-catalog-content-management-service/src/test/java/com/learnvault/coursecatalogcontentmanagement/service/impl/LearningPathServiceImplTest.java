package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.dto.request.LearningPathRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.LearningPathResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.LearningPath;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.LearningPathRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningPathServiceImplTest {

    @Mock
    private LearningPathRepository learningPathRepository;

    @InjectMocks
    private LearningPathServiceImpl learningPathService;

    @Test
    void createPath_success_savesAndReturnsResponse() {
        // Arrange
        LearningPathRequest request = LearningPathRequest.builder()
                .name("Backend Track")
                .targetRole("Backend Developer")
                .courseSequence("1,2,3")
                .totalHours(40)
                .build();
        LearningPath saved = LearningPath.builder()
                .pathId(1)
                .name("Backend Track")
                .targetRole("Backend Developer")
                .courseSequence("1,2,3")
                .totalHours(40)
                .status(LearningPathStatus.ACTIVE)
                .build();
        when(learningPathRepository.save(any(LearningPath.class))).thenReturn(saved);

        // Act
        LearningPathResponse response = learningPathService.createPath(request);

        // Assert
        assertEquals(1, response.getPathId());
        assertEquals("Backend Track", response.getName());
        assertEquals(LearningPathStatus.ACTIVE, response.getStatus());
        verify(learningPathRepository).save(any(LearningPath.class));
    }

    @Test
    void getPathById_notFound_throwsResourceNotFound() {
        // Arrange
        when(learningPathRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> learningPathService.getPathById(99));
    }

    @Test
    void updatePathStatus_success_savesUpdatedStatus() {
        // Arrange: existing ACTIVE path, moving to INACTIVE
        LearningPath existing = LearningPath.builder()
                .pathId(2)
                .name("Data Track")
                .targetRole("Data Engineer")
                .courseSequence("4,5")
                .totalHours(20)
                .status(LearningPathStatus.ACTIVE)
                .build();
        when(learningPathRepository.findById(2)).thenReturn(Optional.of(existing));
        when(learningPathRepository.save(any(LearningPath.class))).thenReturn(existing);

        // Act
        LearningPathResponse response = learningPathService.updatePathStatus(2, LearningPathStatus.INACTIVE);

        // Assert
        assertEquals(LearningPathStatus.INACTIVE, response.getStatus());
        verify(learningPathRepository).save(existing);
    }
}
