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

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Course createCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    @Override
    public List<Course> getCourseDetailsByIds(List<String> courseIds) {
        return courseRepository.findAllById(courseIds);
    }
}
