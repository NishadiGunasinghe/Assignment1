package com.lbu.lbustudent.commons.exeptions;

public class LBUStudentsRuntimeException extends RuntimeException {

    private final String message;
    private final Integer code;

    public LBUStudentsRuntimeException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public LBUStudentsRuntimeException(String message, Integer code, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code;
    }
}
