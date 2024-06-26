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

    /**
     * Handles LBUCourcesRuntimeException by creating a MessageDto with the error message and code from the exception,
     * logging the error, and returning a ResponseEntity with status code 400 (BAD_REQUEST) and the error message.
     *
     * @param ex The LBUCourcesRuntimeException that occurred.
     * @return A ResponseEntity containing a MessageDto with the error message and code.
     */
    @ExceptionHandler(LBUCourcesRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUCourcesRuntimeException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(ex.getCode());
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    /**
     * Handles SQLIntegrityConstraintViolationException by creating a MessageDto with a predefined error message and code,
     * logging the error, and returning a ResponseEntity with status code 400 (BAD_REQUEST) and the error message.
     *
     * @param ex The SQLIntegrityConstraintViolationException that occurred.
     * @return A ResponseEntity containing a MessageDto with the predefined error message and code.
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleException(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(COURSE_ALREADY_AVAILABLE_ERROR.getErrorMessage());
        errorDto.setCode(COURSE_ALREADY_AVAILABLE_ERROR.getErrorCode());
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


}
