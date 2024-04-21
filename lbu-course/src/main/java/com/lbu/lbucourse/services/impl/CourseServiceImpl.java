package com.lbu.lbucourse.services.impl;

import com.lbu.lbucourse.commons.exceptions.LBUCourcesRuntimeException;
import com.lbu.lbucourse.models.Course;
import com.lbu.lbucourse.repositories.CourseRepository;
import com.lbu.lbucourse.services.CourseService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.COURSE_NOT_AVAILABLE;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Retrieves a course by its ID from the database.
     * If the course exists, it returns the course object.
     * If the course doesn't exist, it logs an error and throws an LBUCourcesRuntimeException.
     *
     * @param courseId The ID of the course to retrieve.
     * @return The course object if found.
     * @throws LBUCourcesRuntimeException If the course is not available.
     */
    @Override
    public Course getCourseById(String courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            return optionalCourse.get();
        } else {
            log.error(COURSE_NOT_AVAILABLE.getErrorMessage());
            throw new LBUCourcesRuntimeException(COURSE_NOT_AVAILABLE.getErrorMessage(), COURSE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Deletes a course from the database by its ID.
     * If the course exists, it deletes it from the repository.
     * If the course doesn't exist, it logs an error and throws an LBUCourcesRuntimeException.
     *
     * @param courseId The ID of the course to delete.
     * @throws LBUCourcesRuntimeException If the course is not available.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deleteCourse(String courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            courseRepository.delete(course);
        } else {
            log.error(COURSE_NOT_AVAILABLE.getErrorMessage());
            throw new LBUCourcesRuntimeException(COURSE_NOT_AVAILABLE.getErrorMessage(), COURSE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Creates a new course in the database.
     * It saves the course object and flushes it to the database.
     *
     * @param course The course object to create.
     * @return The created course object.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Course createCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return A list of all courses.
     */
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Updates an existing course in the database.
     * It saves the updated course object and flushes it to the database.
     *
     * @param course The updated course object.
     * @return The updated course object.
     */
    @Override
    public Course updateCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    /**
     * Retrieves course details for multiple course IDs from the database.
     *
     * @param courseIds The list of course IDs to retrieve details for.
     * @return A list of course objects matching the provided IDs.
     */
    @Override
    public List<Course> getCourseDetailsByIds(List<String> courseIds) {
        return courseRepository.findAllById(courseIds);
    }
}
