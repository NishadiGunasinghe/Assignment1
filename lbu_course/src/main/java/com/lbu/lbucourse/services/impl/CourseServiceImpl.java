package com.lbu.lbucourse.services.impl;

import com.lbu.lbucourse.models.Course;
import com.lbu.lbucourse.commons.exceptions.LBUCourseRuntimeException;
import com.lbu.lbucourse.repository.CourseRepository;
import com.lbu.lbucourse.services.CourseService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import static com.lbu.lbucourse.commons.constants.ErrorConstants.COURSE_NOT_AVAILABLE;


@Service
public class CourseServiceImpl implements CourseService{

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
            throw new LBUCourseRuntimeException(COURSE_NOT_AVAILABLE.getErrorMessage(), COURSE_NOT_AVAILABLE.getErrorCode());
        }
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Course createCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    @Override
    public List<Course> getCourseDetailsByIds(List<String> courseIds) {
        return courseRepository.findAllById(courseIds);
    }


}
