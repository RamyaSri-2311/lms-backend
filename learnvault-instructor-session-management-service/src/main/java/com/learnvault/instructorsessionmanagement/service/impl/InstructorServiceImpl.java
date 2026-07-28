package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorStatsResponse;
import com.learnvault.instructorsessionmanagement.dto.response.UserResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.enums.RegistrationSource;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import com.learnvault.instructorsessionmanagement.service.InstructorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserClient userClient;

    @Override
    public InstructorResponse registerInstructor(InstructorRequest request) {
        log.info("Admin registering instructor for user: {}", request.getUserId());
        RegistrationSource source = request.getRegistrationSource() != null
                ? request.getRegistrationSource()
                : RegistrationSource.ADMIN_CREATED;
        return createInstructor(request, source);
    }

    @Override
    public InstructorResponse selfRegisterInstructor(InstructorRequest request) {
        log.info("Instructor self-registering profile for user: {}", request.getUserId());
        return createInstructor(request, RegistrationSource.SELF_REGISTERED);
    }

    private InstructorResponse createInstructor(InstructorRequest request, RegistrationSource source) {
        if (request.getUserId() == null) {
            throw new BadRequestException("userId is required to create an instructor profile");
        }
        if (instructorRepository.existsByUserId(request.getUserId())) {
            throw new BadRequestException(
                    "An instructor profile already exists for user id: " + request.getUserId());
        }

        Instructor instructor = Instructor.builder()
                .userId(request.getUserId())
                .specializations(request.getSpecializations())
                .qualificationLevel(request.getQualificationLevel())
                .experience(request.getExperience())
                .ratingAvg(0.00)
                .status(Status.ACTIVE)
                .registrationSource(source)
                .build();

        Instructor saved = instructorRepository.save(instructor);
        return mapToResponse(saved);
    }

    @Override
    public InstructorResponse getInstructorById(Integer id) {
        log.info("Fetching instructor by ID: {}", id);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        return mapToResponse(instructor);
    }

    @Override
    public InstructorResponse getInstructorByUserId(Integer userId) {
        log.info("Fetching instructor by userId: {}", userId);
        Instructor instructor = instructorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor profile not found for user id: " + userId));
        return mapToResponse(instructor);
    }

    @Override
    public List<InstructorResponse> getAllInstructors() {
        log.info("Fetching all instructors");
        return instructorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstructorResponse> getInstructorsByStatus(String status) {
        log.info("Fetching instructors by status: {}", status);
        Status parsed;
        try {
            parsed = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status: " + status);
        }
        return instructorRepository.findByStatus(parsed).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InstructorStatsResponse getInstructorStats() {
        log.info("Computing instructor stats");
        return InstructorStatsResponse.builder()
                .totalInstructors(instructorRepository.count())
                .activeInstructors(instructorRepository.countByStatus(Status.ACTIVE))
                .selfRegisteredInstructors(
                        instructorRepository.countByRegistrationSource(RegistrationSource.SELF_REGISTERED))
                .adminCreatedInstructors(
                        instructorRepository.countByRegistrationSource(RegistrationSource.ADMIN_CREATED))
                .build();
    }

    @Override
    public InstructorResponse updateRating(Integer id, Double rating) {
        log.info("Updating instructor {} rating to {}", id, rating);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        instructor.setRatingAvg(rating);
        Instructor updated = instructorRepository.save(instructor);
        return mapToResponse(updated);
    }

    @Override
    public InstructorResponse updateStatus(Integer id, String status) {
        log.info("Updating instructor {} status to {}", id, status);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
        try {
            instructor.setStatus(Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status: " + status);
        }
        Instructor updated = instructorRepository.save(instructor);
        return mapToResponse(updated);
    }

    private InstructorResponse mapToResponse(Instructor instructor) {

        String name = null;
        String email = null;
        String phone = null;

        // Enrich with identity details; stay resilient if the identity service is unavailable.
        try {
            UserResponse user = userClient.getUserById(instructor.getUserId());
            if (user != null) {
                name = user.getName();
                email = user.getEmail();
                phone = user.getPhone();
            }
        } catch (Exception ex) {
            log.warn("Could not resolve user {} from identity service: {}",
                    instructor.getUserId(), ex.getMessage());
        }

        return InstructorResponse.builder()
                .instructorId(instructor.getInstructorId())
                .userId(instructor.getUserId())
                .instructorName(name)
                .email(email)
                .phone(phone)
                .specializations(instructor.getSpecializations())
                .qualificationLevel(instructor.getQualificationLevel())
                .experience(instructor.getExperience())
                .ratingAvg(instructor.getRatingAvg())
                .status(instructor.getStatus())
                .registrationSource(instructor.getRegistrationSource())
                .createdDate(instructor.getCreatedDate())
                .build();
    }
}
