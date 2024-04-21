package com.lbu.lbustudent.controllers.impl;

import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.JWT_TOKEN_USER_MISMATCH;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type LBUStudentsRuntimeException. It creates a MessageDto object with the error message
     * and code obtained from the exception. It logs the error message along with the exception stack trace. If the error
     * code is JWT_TOKEN_USER_MISMATCH, it returns a ResponseEntity with status code 403 (FORBIDDEN), otherwise, it
     * returns a ResponseEntity with status code 400 (BAD_REQUEST).
     *
     * @param ex The LBUStudentsRuntimeException caught.
     * @return ResponseEntity<MessageDto> containing the error message and appropriate HTTP status.
     */
    @ExceptionHandler(LBUStudentsRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUStudentsRuntimeException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(ex.getCode());
        log.error("An error occurred {}", ex.getMessage(), ex);
        if (JWT_TOKEN_USER_MISMATCH.getErrorCode().equals(ex.getCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
    }

    /**
     * Handles generic exceptions. It creates a MessageDto object with the error message and sets the code to 500.
     * It logs the error message along with the exception stack trace and returns a ResponseEntity with status code
     * 500 (INTERNAL_SERVER_ERROR).
     *
     * @param ex The Exception caught.
     * @return ResponseEntity<MessageDto> containing the error message and HTTP status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDto> handleException(Exception ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(500);
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }


}
