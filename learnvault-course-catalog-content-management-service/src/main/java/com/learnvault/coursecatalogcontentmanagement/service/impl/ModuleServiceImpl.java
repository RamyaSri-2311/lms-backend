package com.learnvault.coursecatalogcontentmanagement.service.impl;

import com.learnvault.coursecatalogcontentmanagement.dto.request.ModuleRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.ModuleResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.Course;
import com.learnvault.coursecatalogcontentmanagement.entity.Module;
import com.learnvault.coursecatalogcontentmanagement.exception.BadRequestException;
import com.learnvault.coursecatalogcontentmanagement.exception.ResourceNotFoundException;
import com.learnvault.coursecatalogcontentmanagement.repository.CourseRepository;
import com.learnvault.coursecatalogcontentmanagement.repository.ModuleRepository;
import com.learnvault.coursecatalogcontentmanagement.service.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Override
    public ModuleResponse addModule(Integer courseId, ModuleRequest request) {
        log.info("Adding module to course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BadRequestException("Invalid Course ID: " + courseId));

        Module module = Module.builder()
                .course(course)
                .title(request.getTitle())
                .sequenceOrder(request.getSequenceOrder())
                .contentType(request.getContentType())
                .contentURL(request.getContentURL())
                .durationMinutes(request.getDurationMinutes())
                .build();

        Module saved = moduleRepository.save(module);
        return mapToResponse(saved);
    }

    @Override
    public List<ModuleResponse> getModulesByCourse(Integer courseId) {
        log.info("Fetching modules for course: {}", courseId);
        List<Module> modules = moduleRepository.findByCourse_CourseIdOrderBySequenceOrderAsc(courseId);
        return modules.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ModuleResponse getModuleById(Integer moduleId) {
        log.info("Fetching module by ID: {}", moduleId);
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        return mapToResponse(module);
    }

    @Override
    public ModuleResponse updateModule(Integer moduleId, ModuleRequest request) {
        log.info("Updating module: {}", moduleId);
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        module.setTitle(request.getTitle());
        module.setSequenceOrder(request.getSequenceOrder());
        module.setContentType(request.getContentType());
        module.setContentURL(request.getContentURL());
        module.setDurationMinutes(request.getDurationMinutes());

        Module updated = moduleRepository.save(module);
        return mapToResponse(updated);
    }

    @Override
    public void deleteModule(Integer moduleId) {
        log.info("Deleting module: {}", moduleId);
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        moduleRepository.delete(module);
    }

    private ModuleResponse mapToResponse(Module module) {
        return ModuleResponse.builder()
                .moduleId(module.getModuleId())
                .courseId(module.getCourse().getCourseId())
                .title(module.getTitle())
                .sequenceOrder(module.getSequenceOrder())
                .contentType(module.getContentType())
                .contentURL(module.getContentURL())
                .durationMinutes(module.getDurationMinutes())
                .build();
    }
}