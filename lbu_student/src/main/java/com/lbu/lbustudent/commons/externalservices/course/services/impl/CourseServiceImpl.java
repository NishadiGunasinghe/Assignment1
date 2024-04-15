package com.lbu.lbustudent.commons.externalservices.course.services.impl;

import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exeptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.course.services.CourseService;
import com.lbu.lbustudent.dtos.course.CourseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CourseServiceImpl implements CourseService {

    private final RestTemplate courseRestTemplate;

    public CourseServiceImpl(RestTemplate courseRestTemplate) {
        this.courseRestTemplate = courseRestTemplate;
    }

    @Override
    public CourseDto getCourseDetails(String courseHref) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            return courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class).getBody();
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(ErrorConstants.INTERNAL_ERROR.getErrorMessage(), ErrorConstants.INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
