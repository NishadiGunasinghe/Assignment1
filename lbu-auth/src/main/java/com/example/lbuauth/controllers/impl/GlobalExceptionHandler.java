package com.example.lbuauth.controllers.impl;

import com.example.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.example.lbuauth.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.example.lbuauth.commons.constants.ErrorConstants.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles exceptions of type LBUAuthRuntimeException by returning a ResponseEntity
    // with a MessageDto containing the exception message and code, along with a BAD_REQUEST status.
    @ExceptionHandler(LBUAuthRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUAuthRuntimeException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(ex.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Handles exceptions of type LockedException by returning a ResponseEntity
    // with a MessageDto containing a predefined error message and code, along with a BAD_REQUEST status.
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<MessageDto> handleException(LockedException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_NOT_ENABLE_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Handles exceptions of type SQLIntegrityConstraintViolationException by returning a ResponseEntity
    // with a MessageDto containing a predefined error message and code, along with a BAD_REQUEST status.
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleException(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_ALREADY_AVAILABLE_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_ALREADY_AVAILABLE_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Handles exceptions of type InternalAuthenticationServiceException by returning a ResponseEntity
    // with a MessageDto containing a predefined error message and code, along with a FORBIDDEN status.
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<MessageDto> handleException(InternalAuthenticationServiceException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

}
