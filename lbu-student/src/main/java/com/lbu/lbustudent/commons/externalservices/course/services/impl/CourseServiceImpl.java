package com.lbu.lbustudent.commons.externalservices.course.services.impl;

import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.course.services.CourseService;
import com.lbu.lbustudent.dtos.course.CourseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CourseServiceImpl implements CourseService {

    private final RestTemplate courseRestTemplate;

    public CourseServiceImpl(RestTemplate courseRestTemplate) {
        this.courseRestTemplate = courseRestTemplate;
    }

    @Override
    public CourseDto getCourseDetails(String courseHref, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<CourseDto> courseDtoResponseEntity = courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class);
            return courseDtoResponseEntity.getBody();
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(ErrorConstants.COURSE_SERVICE_GET_ERROR.getErrorMessage(), ErrorConstants.COURSE_SERVICE_GET_ERROR.getErrorCode(), e);
        }
    }
}
