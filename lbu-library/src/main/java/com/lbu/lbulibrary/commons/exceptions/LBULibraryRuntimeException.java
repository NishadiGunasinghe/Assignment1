package com.lbu.lbulibrary.commons.exceptions;

import lombok.Getter;

@Getter
public class LBULibraryRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBULibraryRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBULibraryRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
