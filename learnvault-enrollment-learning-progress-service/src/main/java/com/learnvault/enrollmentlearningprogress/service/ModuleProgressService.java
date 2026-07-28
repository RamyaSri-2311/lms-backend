package com.learnvault.enrollmentlearningprogress.service;

import com.learnvault.enrollmentlearningprogress.dto.response.ModuleProgressResponse;

import java.util.List;

public interface ModuleProgressService {
    List<ModuleProgressResponse> getProgressByEnrollment(Integer enrollmentId);
    ModuleProgressResponse updateModuleStatus(Integer enrollmentId, Integer moduleId, String status, Integer timeSpentMinutes);
}