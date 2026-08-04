package com.learnvault.enrollmentlearningprogress.client.fallback;

import com.learnvault.enrollmentlearningprogress.client.CourseCatalogClient;
import com.learnvault.enrollmentlearningprogress.client.dto.ModuleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback for {@link CourseCatalogClient}.
 * Used by the circuit breaker when the course-catalog-content-management-service is unavailable.
 */
@Slf4j
@Component
public class CourseCatalogClientFallback implements CourseCatalogClient {

    @Override
    public List<ModuleDto> getModules(Integer courseId) {
        // course-catalog service is down: return an empty module list.
        log.warn("Fallback: course-catalog-content-management-service unavailable, returning empty modules for courseId={}", courseId);
        return Collections.emptyList();
    }
}
