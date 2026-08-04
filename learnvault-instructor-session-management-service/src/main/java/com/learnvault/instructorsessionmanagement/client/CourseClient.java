package com.learnvault.instructorsessionmanagement.client;

import com.learnvault.instructorsessionmanagement.client.dto.CourseDto;
import com.learnvault.instructorsessionmanagement.client.fallback.CourseClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-catalog-content-management-service", fallback = CourseClientFallback.class)
public interface CourseClient {

    @GetMapping("/api/courses/{id}")
    CourseDto getCourseById(@PathVariable("id") Integer id);
}
