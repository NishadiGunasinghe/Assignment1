package com.example.lbuauth.commons.exceptions;

import lombok.Getter;

@Getter
public class LBUAuthRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBUAuthRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBUAuthRuntimeException(String message, Throwable throwable, Integer code) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
