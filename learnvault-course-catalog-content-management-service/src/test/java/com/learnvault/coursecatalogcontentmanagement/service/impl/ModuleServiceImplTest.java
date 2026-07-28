package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.dto.request.ModuleRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.ModuleResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.Module;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.ContentType;
import com.learnvault.coursecatalogcontentmanagement.exception.BadRequestException;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.CourseRepository;
import com.learnvault.coursecatalogcontentmanagement.repository.ModuleRepository;
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
class ModuleServiceImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    @Test
    void addModule_success_savesAndReturnsResponse() {
        // Arrange: parent course exists
        Course course = Course.builder().courseId(1).title("Java Basics").build();
        ModuleRequest request = ModuleRequest.builder()
                .title("Intro")
                .sequenceOrder(1)
                .contentType(ContentType.VIDEO)
                .contentURL("http://x/1")
                .durationMinutes(15)
                .build();
        Module saved = Module.builder()
                .moduleId(10)
                .course(course)
                .title("Intro")
                .sequenceOrder(1)
                .contentType(ContentType.VIDEO)
                .contentURL("http://x/1")
                .durationMinutes(15)
                .build();
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(moduleRepository.save(any(Module.class))).thenReturn(saved);

        // Act
        ModuleResponse response = moduleService.addModule(1, request);

        // Assert
        assertEquals(10, response.getModuleId());
        assertEquals(1, response.getCourseId());
        assertEquals("Intro", response.getTitle());
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    void getModuleById_notFound_throwsResourceNotFound() {
        // Arrange
        when(moduleRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> moduleService.getModuleById(99));
    }

    @Test
    void addModule_invalidCourse_throwsBadRequest() {
        // Arrange: parent course does not exist
        ModuleRequest request = ModuleRequest.builder()
                .title("Intro")
                .sequenceOrder(1)
                .contentType(ContentType.VIDEO)
                .build();
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> moduleService.addModule(99, request));
        verify(moduleRepository, never()).save(any(Module.class));
    }
}
