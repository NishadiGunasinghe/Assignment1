package com.lbu.lbucourse.commons.exceptions;

import lombok.Getter;

@Getter
public class LBUCourseRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBUCourseRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBUCourseRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
