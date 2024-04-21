package com.lbu.lbucourse.commons.exceptions;

import lombok.Getter;

@Getter
public class LBUCourcesRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBUCourcesRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBUCourcesRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
