package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.JWT_TOKEN_USER_MISMATCH;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type LBUFinanceRuntimeException. It creates a MessageDto object with the error message and code
     * from the exception, logs the error, and returns a ResponseEntity containing the error details with an appropriate
     * HTTP status code. If the error code is related to JWT token user mismatch, it returns a FORBIDDEN status code;
     * otherwise, it returns a BAD_REQUEST status code.
     *
     * @param ex The LBUFinanceRuntimeException object to handle.
     * @return A ResponseEntity containing the error message and code.
     */
    @ExceptionHandler(LBUFinanceRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUFinanceRuntimeException ex) {
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


}
