package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.client.InstructorClient;
import com.learnvault.coursecatalogcontentmanagement.client.dto.InstructorDto;
import com.learnvault.coursecatalogcontentmanagement.dto.request.CourseRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.CourseResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;
import com.learnvault.coursecatalogcontentmanagement.exception.BadRequestException;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.CourseRepository;
import com.learnvault.coursecatalogcontentmanagement.service.CourseService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorClient instructorClient;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creating course: {}", request.getTitle());

        String title = request.getTitle() != null ? request.getTitle().trim() : null;

        // Prevent duplicate courses (same title, case-insensitive)
        if (title != null && !title.isEmpty() && courseRepository.existsByTitleIgnoreCase(title)) {
            throw new BadRequestException("A course titled \"" + title + "\" already exists.");
        }

        // Validate the assigned instructor exists and is active (if one is assigned)
        if (request.getInstructorId() != null) {
            validateInstructorActive(request.getInstructorId());
        }

        Course course = Course.builder()
                .title(title)
                .category(request.getCategory())
                .level(request.getLevel())
                .durationHours(request.getDurationHours())
                .instructorId(request.getInstructorId())
                .deliveryMode(request.getDeliveryMode())
                .status(CourseStatus.DRAFT)
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created with ID: {}", saved.getCourseId());
        return mapToResponse(saved);
    }

    @Override
    public CourseResponse updateCourse(Integer id, CourseRequest request) {
        log.info("Updating course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        String title = request.getTitle() != null ? request.getTitle().trim() : null;

        // Prevent renaming to a title already used by another course
        if (title != null && !title.isEmpty()
                && courseRepository.existsByTitleIgnoreCaseAndCourseIdNot(title, id)) {
            throw new BadRequestException("Another course titled \"" + title + "\" already exists.");
        }

        // Validate instructor only when it actually changes
        if (request.getInstructorId() != null
                && !request.getInstructorId().equals(course.getInstructorId())) {
            validateInstructorActive(request.getInstructorId());
        }

        if (title != null && !title.isEmpty()) course.setTitle(title);
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getDurationHours() != null) course.setDurationHours(request.getDurationHours());
        if (request.getInstructorId() != null) course.setInstructorId(request.getInstructorId());
        if (request.getDeliveryMode() != null) course.setDeliveryMode(request.getDeliveryMode());

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    public CourseResponse getCourseById(Integer id) {
        log.info("Fetching course by ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses(CourseStatus status, String category, CourseLevel level, Integer instructorId) {
        log.info("Fetching courses with filters - status: {}, category: {}, level: {}, instructorId: {}",
                status, category, level, instructorId);

        List<Course> courses;
        if (instructorId != null) {
            courses = courseRepository.findByInstructorId(instructorId);
        } else if (status != null) {
            courses = courseRepository.findByStatus(status);
        } else if (category != null) {
            courses = courseRepository.findByCategory(category);
        } else if (level != null) {
            courses = courseRepository.findByLevel(level);
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void validateInstructorActive(Integer instructorId) {
        InstructorDto instructor;
        try {
            instructor = instructorClient.getInstructorById(instructorId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Assigned instructor does not exist: " + instructorId);
        }
        if (instructor == null) {
            throw new BadRequestException("Assigned instructor does not exist: " + instructorId);
        }
        if (instructor.getStatus() == null || !"ACTIVE".equalsIgnoreCase(instructor.getStatus())) {
            throw new BadRequestException("Assigned instructor is not active: " + instructorId);
        }
    }

    @Override
    public CourseResponse publishCourse(Integer id) {
        log.info("Publishing course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT courses can be published");
        }
        
        course.setStatus(CourseStatus.PUBLISHED);
        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    public CourseResponse archiveCourse(Integer id) {
        log.info("Archiving course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only PUBLISHED courses can be archived");
        }
        
        course.setStatus(CourseStatus.ARCHIVED);
        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    public void deleteCourse(Integer id) {
        log.info("Deleting course: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .category(course.getCategory())
                .level(course.getLevel())
                .durationHours(course.getDurationHours())
                .instructorId(course.getInstructorId())
                .deliveryMode(course.getDeliveryMode())
                .status(course.getStatus())
                .build();
    }
}