package com.learnvault.instructorsessionmanagement.service.impl;

import com.learnvault.instructorsessionmanagement.client.UserClient;
import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.dto.response.UserResponse;
import com.learnvault.instructorsessionmanagement.entity.Instructor;
import com.learnvault.instructorsessionmanagement.entity.enums.RegistrationSource;
import com.learnvault.instructorsessionmanagement.entity.enums.Status;
import com.learnvault.instructorsessionmanagement.exception.BadRequestException;
import com.learnvault.instructorsessionmanagement.exception.ResourceNotFoundException;
import com.learnvault.instructorsessionmanagement.repository.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private InstructorServiceImpl instructorService;

    @Test
    void registerInstructor_success_savesAndReturnsResponse() {
        // Arrange
        InstructorRequest request = InstructorRequest.builder()
                .userId(10).specializations("Java").qualificationLevel("MSc").experience("5y").build();
        when(instructorRepository.existsByUserId(10)).thenReturn(false);
        Instructor saved = Instructor.builder()
                .instructorId(1).userId(10).status(Status.ACTIVE)
                .registrationSource(RegistrationSource.ADMIN_CREATED).ratingAvg(0.0).build();
        when(instructorRepository.save(any(Instructor.class))).thenReturn(saved);
        UserResponse user = new UserResponse();
        user.setName("Alice");
        when(userClient.getUserById(10)).thenReturn(user);

        // Act
        InstructorResponse response = instructorService.registerInstructor(request);

        // Assert
        assertEquals(10, response.getUserId());
        assertEquals("Alice", response.getInstructorName());
        verify(instructorRepository).save(any(Instructor.class));
    }

    @Test
    void getInstructorById_notFound_throwsResourceNotFound() {
        // Arrange
        when(instructorRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> instructorService.getInstructorById(99));
    }

    @Test
    void registerInstructor_duplicateUser_throwsBadRequest() {
        // Arrange
        InstructorRequest request = InstructorRequest.builder().userId(10).build();
        when(instructorRepository.existsByUserId(10)).thenReturn(true);

        // Act + Assert
        assertThrows(BadRequestException.class, () -> instructorService.registerInstructor(request));
        verify(instructorRepository, never()).save(any(Instructor.class));
    }
}
