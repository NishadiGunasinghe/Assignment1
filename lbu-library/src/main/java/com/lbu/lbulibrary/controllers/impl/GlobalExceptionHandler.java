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

    /**
     * Handles exceptions of type LBULibraryRuntimeException. It constructs a MessageDto with the error message and code
     * from the exception, logs the error, and returns an appropriate ResponseEntity. If the error code indicates a JWT
     * token user mismatch, it returns a FORBIDDEN status; otherwise, it returns a BAD_REQUEST status.
     *
     * @param ex The LBULibraryRuntimeException to handle.
     * @return A ResponseEntity containing the error message and status.
     */
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

    /**
     * Handles exceptions of type SQLIntegrityConstraintViolationException. It constructs a MessageDto with the error
     * message and a predefined error code, logs the error, and returns a ResponseEntity with a BAD_REQUEST status.
     *
     * @param ex The SQLIntegrityConstraintViolationException to handle.
     * @return A ResponseEntity containing the error message and status.
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleSqlIntegrateViolation(SQLIntegrityConstraintViolationException ex) {
        MessageDto errorDto = new MessageDto();
        errorDto.setMessage(ex.getMessage());
        errorDto.setCode(400);
        log.error("An error occurred {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
