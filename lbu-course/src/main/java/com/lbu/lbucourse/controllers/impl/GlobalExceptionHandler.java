package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.commons.exceptions.LBUCourcesRuntimeException;
import com.lbu.lbucourse.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.COURSE_ALREADY_AVAILABLE_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LBUCourcesRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUCourcesRuntimeException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(ex.getCode());
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleException(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(COURSE_ALREADY_AVAILABLE_ERROR.getErrorMessage());
        errorDto.setCode(COURSE_ALREADY_AVAILABLE_ERROR.getErrorCode());
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

}
