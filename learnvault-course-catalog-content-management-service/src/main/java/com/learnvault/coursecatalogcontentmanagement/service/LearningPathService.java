package com.learnvault.coursecatalogcontentmanagement.service;

import com.learnvault.coursecatalogcontentmanagement.dto.request.LearningPathRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.LearningPathResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.LearningPathStatus;

import java.util.List;

public interface LearningPathService {
    LearningPathResponse createPath(LearningPathRequest request);
    LearningPathResponse getPathById(Integer id);
    List<LearningPathResponse> getAllPaths();
    LearningPathResponse updatePathStatus(Integer id, LearningPathStatus status);
    void deletePath(Integer id);
}