package com.learnvault.coursecatalogcontentmanagement.repository;

import com.learnvault.coursecatalogcontentmanagement.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    List<Module> findByCourse_CourseId(Integer courseId);
    List<Module> findByCourse_CourseIdOrderBySequenceOrderAsc(Integer courseId);
}