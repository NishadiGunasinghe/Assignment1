package com.lbu.lbucourse.commons.exceptions;

import lombok.Getter;

@Getter
public class LBUFinanceRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBUFinanceRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBUFinanceRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
