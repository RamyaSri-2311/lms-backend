package com.learnvault.coursecatalogcontentmanagement.client;

import com.learnvault.coursecatalogcontentmanagement.client.dto.InstructorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "instructor-session-management-service")
public interface InstructorClient {

    @GetMapping("/api/instructors/id/{id}")
    InstructorDto getInstructorById(@PathVariable("id") Integer id);
}
