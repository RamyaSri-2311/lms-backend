package com.learnvault.enrollmentlearningprogress.client;

import com.learnvault.enrollmentlearningprogress.client.dto.ModuleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "course-catalog-content-management-service")
public interface CourseCatalogClient {

    @GetMapping("/api/courses/{courseId}/modules")
    List<ModuleDto> getModules(@PathVariable("courseId") Integer courseId);
}
