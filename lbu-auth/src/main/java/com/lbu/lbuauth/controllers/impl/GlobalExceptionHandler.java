package com.lbu.lbuauth.controllers.impl;

import com.lbu.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.lbu.lbuauth.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.lbu.lbuauth.commons.constants.ErrorConstants.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LBUAuthRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBUAuthRuntimeException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(ex.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<MessageDto> handleException(LockedException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_NOT_ENABLE_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleException(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_ALREADY_AVAILABLE_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_ALREADY_AVAILABLE_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<MessageDto> handleException(InternalAuthenticationServiceException ex) {
        MessageDto errorDto = new MessageDto();
        log.error("An error occurred", ex);
        errorDto.setMessage(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage());
        errorDto.setCode(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorCode());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

}
