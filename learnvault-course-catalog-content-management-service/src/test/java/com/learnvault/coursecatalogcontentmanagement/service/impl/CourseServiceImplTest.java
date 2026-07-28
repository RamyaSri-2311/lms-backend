package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.client.InstructorClient;
import com.learnvault.coursecatalogcontentmanagement.dto.request.CourseRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.CourseResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.DeliveryMode;
import com.learnvault.coursecatalogcontentmanagement.exception.BadRequestException;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.CourseRepository;
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
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private InstructorClient instructorClient;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourse_success_savesAndReturnsResponse() {
        // Arrange: a valid request with no instructor (skips Feign validation)
        CourseRequest request = CourseRequest.builder()
                .title("Java Basics")
                .category("Programming")
                .level(CourseLevel.BEGINNER)
                .durationHours(10)
                .deliveryMode(DeliveryMode.SELFPACED)
                .build();
        Course saved = Course.builder()
                .courseId(1)
                .title("Java Basics")
                .category("Programming")
                .level(CourseLevel.BEGINNER)
                .durationHours(10)
                .deliveryMode(DeliveryMode.SELFPACED)
                .status(CourseStatus.DRAFT)
                .build();
        when(courseRepository.existsByTitleIgnoreCase("Java Basics")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        // Act
        CourseResponse response = courseService.createCourse(request);

        // Assert
        assertEquals(1, response.getCourseId());
        assertEquals("Java Basics", response.getTitle());
        assertEquals(CourseStatus.DRAFT, response.getStatus());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void getCourseById_notFound_throwsResourceNotFound() {
        // Arrange: repository has no course with this id
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(99));
    }

    @Test
    void createCourse_duplicateTitle_throwsBadRequest() {
        // Arrange: a course with the same title already exists
        CourseRequest request = CourseRequest.builder()
                .title("Java Basics")
                .category("Programming")
                .level(CourseLevel.BEGINNER)
                .durationHours(10)
                .deliveryMode(DeliveryMode.SELFPACED)
                .build();
        when(courseRepository.existsByTitleIgnoreCase("Java Basics")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> courseService.createCourse(request));
        verify(courseRepository, never()).save(any(Course.class));
    }
}
