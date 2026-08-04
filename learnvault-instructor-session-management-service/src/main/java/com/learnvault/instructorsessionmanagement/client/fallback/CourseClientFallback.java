package com.learnvault.instructorsessionmanagement.client.fallback;

import com.learnvault.instructorsessionmanagement.client.CourseClient;
import com.learnvault.instructorsessionmanagement.client.dto.CourseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link CourseClient}. Returns safe defaults when the
 * course-catalog-content-management-service is unavailable.
 */
@Slf4j
@Component
public class CourseClientFallback implements CourseClient {

    @Override
    public CourseDto getCourseById(Integer id) {
        log.warn("CourseClient fallback: getCourseById({}) - service unavailable, returning null", id);
        return null;
    }
}
