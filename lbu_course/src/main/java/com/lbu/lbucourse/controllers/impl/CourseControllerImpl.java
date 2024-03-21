package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.controllers.CourseController;
import com.lbu.lbucourse.dtos.MessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseControllerImpl implements CourseController {
    @Override
    public ResponseEntity<MessageDto> createUser(MessageDto userDto) {
        return null;
    }
}
