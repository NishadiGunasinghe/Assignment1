package com.lbu.lbustudent.commons.externalservices.course.services;

import com.lbu.lbustudent.dtos.course.CourseDto;

public interface CourseService {
    CourseDto getCourseDetails(String courseHref);
}
