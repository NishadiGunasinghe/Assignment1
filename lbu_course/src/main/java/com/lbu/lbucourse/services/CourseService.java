package com.lbu.lbucourse.services;

import com.lbu.lbucourse.models.Course;

import java.util.List;

public interface CourseService {

    //To search a course by its ID
    Course getCourseById(String courseId);

    List<Course> getAllCourses();

    Course createCourse(Course course);

    List<Course> getCourseDetailsByIds(List<String> courseIds);
}

