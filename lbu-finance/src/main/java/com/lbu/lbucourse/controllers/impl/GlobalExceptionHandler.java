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
