package com.learnvault.coursecatalogcontentmanagement.service;

import com.learnvault.coursecatalogcontentmanagement.dto.request.CourseRequest;
import com.learnvault.coursecatalogcontentmanagement.dto.response.CourseResponse;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseLevel;
import com.learnvault.coursecatalogcontentmanagement.entity.enums.CourseStatus;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Integer id, CourseRequest request);
    CourseResponse getCourseById(Integer id);
    List<CourseResponse> getAllCourses(CourseStatus status, String category, CourseLevel level, Integer instructorId);
    CourseResponse publishCourse(Integer id);
    CourseResponse archiveCourse(Integer id);
    void deleteCourse(Integer id);
}