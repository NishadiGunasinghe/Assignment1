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

    /**
     * Retrieves details of multiple courses based on the provided list of course IDs. Validates each course ID, retrieves
     * course details from the service layer, maps them to DTOs, and constructs a response entity with the course DTOs.
     * If any exception occurs during the conversion or retrieval process, it is logged and rethrown as an LBUCourcesRuntimeException.
     *
     * @param courseIds List of course IDs to retrieve details for.
     * @return ResponseEntity containing CourseDtos with details of the retrieved courses.
     */

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

    /**
     * Retrieves details of a single course based on the provided course ID. Validates the course ID, retrieves the course
     * details from the service layer, maps it to a DTO, and constructs a response entity with the course DTO.
     * If any exception occurs during the conversion or retrieval process, it is logged and rethrown as an LBUCourcesRuntimeException.
     *
     * @param courseId The ID of the course to retrieve details for.
     * @return ResponseEntity containing CourseDto with details of the retrieved course.
     */

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

    /**
     * Deletes the course with the specified course ID. Validates the course ID and deletes the course via the service layer.
     * Constructs a message DTO indicating successful deletion and returns it in a response entity.
     *
     * @param courseId The ID of the course to delete.
     * @return ResponseEntity containing a MessageDto indicating successful deletion.
     */

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

    /**
     * Creates a new course based on the provided CourseDto. Validates the course DTO, maps it to a Course entity, and
     * creates the course via the service layer. Constructs a response entity containing the created course DTO.
     * If any exception occurs during the conversion or creation process, it is logged and rethrown as an LBUCourcesRuntimeException.
     *
     * @param courseDto The DTO containing details of the course to be created.
     * @return ResponseEntity containing the created CourseDto.
     */

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

    /**
     * Updates an existing course based on the provided CourseDto. Validates the course DTO, maps it to a Course entity,
     * updates the course via the service layer, and constructs a response entity containing the updated course DTO.
     * If any exception occurs during the conversion or update process, it is logged and rethrown as an LBUCourcesRuntimeException.
     *
     * @param courseDto The DTO containing details of the course to be updated.
     * @return ResponseEntity containing the updated CourseDto.
     */

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

    /**
     * Retrieves details of all available courses. Retrieves all courses from the service layer, maps them to DTOs,
     * and constructs a response entity with the course DTOs. If any exception occurs during the conversion or retrieval process,
     * it is logged and rethrown as an LBUCourcesRuntimeException.
     *
     * @return ResponseEntity containing CourseDtos with details of all available courses.
     */

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
