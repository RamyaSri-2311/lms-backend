package com.learnvault.instructorsessionmanagement.service;

import com.learnvault.instructorsessionmanagement.dto.request.InstructorRequest;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorResponse;
import com.learnvault.instructorsessionmanagement.dto.response.InstructorStatsResponse;

import java.util.List;

public interface InstructorService {
    InstructorResponse registerInstructor(InstructorRequest request);
    InstructorResponse selfRegisterInstructor(InstructorRequest request);
    InstructorResponse getInstructorById(Integer id);
    InstructorResponse getInstructorByUserId(Integer userId);
    List<InstructorResponse> getAllInstructors();
    List<InstructorResponse> getInstructorsByStatus(String status);
    InstructorStatsResponse getInstructorStats();
    InstructorResponse updateRating(Integer id, Double rating);
    InstructorResponse updateStatus(Integer id, String status);
}
