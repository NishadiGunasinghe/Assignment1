package com.lbu.lbulibrary.controllers.impl;

import com.lbu.lbulibrary.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbulibrary.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.lbu.lbulibrary.commons.constants.ErrorConstants.JWT_TOKEN_USER_MISMATCH;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LBULibraryRuntimeException.class)
    public ResponseEntity<MessageDto> handleException(LBULibraryRuntimeException ex) {
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

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleSqlIntegrateViolation(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(400);
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

}
