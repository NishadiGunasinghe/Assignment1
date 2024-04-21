package com.lbu.lbucourse.services;

import com.lbu.lbucourse.models.Course;

import java.util.List;

public interface CourseService {
    Course getCourseById(String courseId);

    void deleteCourse(String courseId);

    Course createCourse(Course course);

    List<Course> getAllCourses();

    Course updateCourse(Course course);

    List<Course> getCourseDetailsByIds(List<String> courseIds);
}
