package com.learnvault.coursecatalogcontentmanagement.service;

import com.learnvault.coursecatalogcontentmanagement.dto.request.ModuleRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.ModuleResponse;

import java.util.List;

public interface ModuleService {
    ModuleResponse addModule(Integer courseId, ModuleRequest request);
    List<ModuleResponse> getModulesByCourse(Integer courseId);
    ModuleResponse getModuleById(Integer moduleId);
    ModuleResponse updateModule(Integer moduleId, ModuleRequest request);
    void deleteModule(Integer moduleId);
}