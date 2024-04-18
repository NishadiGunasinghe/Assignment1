package com.lbu.lbufinance.commons.exceptions;

import lombok.Getter;

@Getter
public class LbuFinanceRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LbuFinanceRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LbuFinanceRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
