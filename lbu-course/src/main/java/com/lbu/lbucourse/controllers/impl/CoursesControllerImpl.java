package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.commons.exceptions.LBUCourcesRuntimeException;
import com.lbu.lbucourse.controllers.CoursesController;
import com.lbu.lbucourse.dtos.CourseDto;
import com.lbu.lbucourse.dtos.CourseDtos;
import com.lbu.lbucourse.dtos.MessageDto;
import com.lbu.lbucourse.models.Course;
import com.lbu.lbucourse.services.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class CoursesControllerImpl implements CoursesController {

    private final ModelMapper modelMapper;
    private final CourseService courseService;

    public CoursesControllerImpl(ModelMapper modelMapper, CourseService courseService) {
        this.modelMapper = modelMapper;
        this.courseService = courseService;
    }

    @Override
    public ResponseEntity<CourseDtos> getCourses(List<String> courseIds) {
        for (String courseId : courseIds) {
            validateCourseId(courseId);
        }
        log.info("get the course id {}", courseIds);
        List<Course> courses = courseService.getCourseDetailsByIds(courseIds);
        try {
            CourseDtos courseDtos = new CourseDtos();
            List<CourseDto> coursesList = courses.stream().map(course -> {
                CourseDto courseDto = modelMapper.map(course, CourseDto.class);
                courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
                return courseDto;
            }).collect(Collectors.toList());
            courseDtos.setCourses(coursesList);
            log.info("get the courses {}", coursesList.size());
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            log.error("Model conversion error");
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<CourseDto> getCourse(String courseId) {
        validateCourseId(courseId);
        log.info("get the course id {}", courseId);
        Course course = courseService.getCourseById(courseId);
        try {
            CourseDto courseDto = modelMapper.map(course, CourseDto.class);
            courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
            log.info("get the courses {}", courseDto);
            return ResponseEntity.ok(courseDto);
        } catch (Exception e) {
            log.error("Model conversion error");
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public ResponseEntity<MessageDto> deleteCourse(String courseId) {
        validateCourseId(courseId);
        log.info("delete the course id {}", courseId);
        courseService.deleteCourse(courseId);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("Successfully deleted the course");
        messageDto.setCode(200);
        return ResponseEntity.ok(messageDto);
    }

    @Override
    public ResponseEntity<CourseDto> createCourse(CourseDto courseDto) {
        validateCourses(courseDto);
        log.info("creating the course {}", courseDto);
        Course course;
        try {
            course = modelMapper.map(courseDto, Course.class);
        } catch (Exception e) {
            log.error("Model conversion error {}", courseDto);
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Course createdCourse = courseService.createCourse(course);
        try {
            courseDto = modelMapper.map(createdCourse, CourseDto.class);
            courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
        } catch (Exception e) {
            log.error("Model conversion error {}", courseDto);
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        return ResponseEntity.ok(courseDto);
    }

    @Override
    public ResponseEntity<CourseDto> updateCourse(CourseDto courseDto) {
        validateCoursesWithId(courseDto);
        log.info("updating the course {}", courseDto);
        Course course;
        try {
            course = modelMapper.map(courseDto, Course.class);
            String[] parts = courseDto.getIdHref().split("/");
            course.setId(parts[parts.length - 1]);
        } catch (Exception e) {
            log.error("Model conversion error {}", courseDto);
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Course updatedCourse = courseService.updateCourse(course);
        try {
            courseDto = modelMapper.map(updatedCourse, CourseDto.class);
            courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
        } catch (Exception e) {
            log.error("Model conversion error {}", courseDto);
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        return ResponseEntity.ok(courseDto);
    }

    @Override
    public ResponseEntity<CourseDtos> getCourses() {
        List<Course> courses = courseService.getAllCourses();
        log.info("get all the courses size: {}", courses.size());
        try {
            CourseDtos courseDtos = new CourseDtos();
            courseDtos.setCourses(courses.stream().map(course -> {
                CourseDto courseDto = modelMapper.map(course, CourseDto.class);
                courseDto.setIdHref(REST_COURSES_COURSE_ID.replace("{courseId}", course.getId()));
                return courseDto;
            }).collect(Collectors.toList()));
            log.info("get all the courses size: {}", courseDtos.getCourses().size());
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            log.error("Model conversion error size: {}", courses.size());
            throw new LBUCourcesRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
