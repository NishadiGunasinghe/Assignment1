package com.lbu.lbuauth.commons.constants;

import lombok.Getter;

@Getter
public enum SuccessConstants {

    ACCOUNT_ACTIVATED("Account successfully activated.", 2000),
    ACCOUNT_SEND_ACTIVATION("Sending activation token again.", 2001),
    JWT_VALID_TOKEN("Given jwt token is valid.", 2002);

    private final String message;
    private final Integer code;

    SuccessConstants(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
