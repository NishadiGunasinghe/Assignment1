package com.lbu.lbufinance.commons.constants;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    JWT_TOKEN_SECRET_NOT_AVAILABLE("Token secret is not available.", 6000),
    JWT_TOKEN_EXPIRED("Given token is expired.", 6001),
    JWT_TOKEN_INVALID_SIGNATURE("Invalid token provided.", 6002),
    JWT_TOKEN_NOT_AVAILABLE("Authorization header does not have a token.", 6003),
    JWT_TOKEN_USER_NOT_AVAILABLE("Given User not available.", 6004),
    JWT_TOKEN_USER_MISMATCH("Invalid user access.", 6005),
    JWT_TOKEN_INVALID("Invalid token.", 6006),

    ACCOUNT_NOT_AVAILABLE("Given account id is not available.", 10000),
    ACCOUNT_NOT_VALID_AVAILABLE("Given account is not valid.", 10002),
    INVOICE_NOT_AVAILABLE("Given invoice is not available.", 10003),

    INTERNAL_ERROR("An error occurred.", 9000),
    INVALID_ERROR("Invalid Data provided.", 9003),
    INVALID_UUID("Invalid UUID provided.", 9001),
    INVALID_HREF("Invalid href provided.", 9002);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
