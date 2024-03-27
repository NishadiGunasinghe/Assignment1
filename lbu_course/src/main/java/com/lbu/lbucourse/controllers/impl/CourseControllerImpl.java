package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.controllers.CourseController;
import com.lbu.lbucourse.dtos.CourseDto;
import com.lbu.lbucourse.dtos.CourseDtos;
import com.lbu.lbucourse.models.Course;
import com.lbu.lbucourse.services.CourseService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.lbu.lbucourse.commons.exceptions.LBUCourseRuntimeException;

import java.util.List;
import java.util.stream.Collectors;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.INTERNAL_ERROR;

@RestController
public class CourseControllerImpl implements CourseController {

    private final ModelMapper modelMapper;
    private final CourseService courseService;

    public CourseControllerImpl(ModelMapper modelMapper, CourseService courseService) {
        this.modelMapper = modelMapper;
        this.courseService = courseService;
    }

    @Override
    public ResponseEntity<CourseDtos> getCourses(List<String> courseIds) {
        // Validate each course ID in the provided list
        for (String courseId : courseIds) {
            validateCourseId(courseId);
        }

        // Retrieve details of courses corresponding to the provided IDs
        List<Course> courses = courseService.getCourseDetailsByIds(courseIds);
        try {
            // Map Course entities to CourseDto objects and set additional data
            CourseDtos courseDtos = new CourseDtos();
            List<CourseDto> coursesList = courses.stream().map(course -> {
                // Map Course entity to CourseDto using ModelMapper
                CourseDto courseDto = modelMapper.map(course, CourseDto.class);
                // Set href for each CourseDto based on REST endpoint template
                courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
                return courseDto;
            }).collect(Collectors.toList());
            // Set the list of CourseDto objects in the CourseDtos wrapper
            courseDtos.setCourses(coursesList);
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            throw new LBUCourseRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<CourseDto> getCourse(String courseId) {
        // Validate the course ID
        validateCourseId(courseId);
        // Retrieve the course object corresponding to the given ID
        Course course = courseService.getCourseById(courseId);
        try {
            // Convert the Course object to CourseDto using ModelMapper
            CourseDto courseDto = modelMapper.map(course, CourseDto.class);
            // Set the ID Href in the CourseDto using a predefined REST endpoint template
            courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));

            // Return ResponseEntity with HTTP status OK (200) and the CourseDto
            return ResponseEntity.ok(courseDto);
        } catch (Exception e) {
            throw new LBUCourseRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<CourseDto> createCourse(CourseDto courseDto) {
        // Validate the input courseDto
        validateCourses(courseDto);
        Course course;
        try {
            // Map the CourseDto to a Course entity
            course = modelMapper.map(courseDto, Course.class);
        } catch (Exception e) {
            // If mapping fails, throw a runtime exception with an internal error message and error code
            throw new LBUCourseRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        // Create the course in the service layer
        Course createdCourse = courseService.createCourse(course);

        try {
            // Map the created Course entity back to a CourseDto
            courseDto = modelMapper.map(createdCourse, CourseDto.class);
            // Set the self-referencing link for the created course
            courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
        } catch (Exception e) {
            throw new LBUCourseRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }

        // Return a ResponseEntity with the created CourseDto and HTTP status OK (200)
        return ResponseEntity.ok(courseDto);
    }

    @Override
    public ResponseEntity<CourseDtos> getCourses() {
        // Retrieve all courses from the course service
        List<Course> courses = courseService.getAllCourses();
        try {
            // Create a new CourseDtos object to hold DTO representations of courses
            CourseDtos courseDtos = new CourseDtos();
            // Map each Course object to a CourseDto object using modelMapper
            // Add a custom URL for each CourseDto object based on its ID
            courseDtos.setCourses(courses.stream().map(course -> {
                CourseDto courseDto = modelMapper.map(course, CourseDto.class);
                courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
                return courseDto;
            }).collect(Collectors.toList()));
            // Return a ResponseEntity with the DTOs of all courses
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            throw new LBUCourseRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

}
