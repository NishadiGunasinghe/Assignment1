package com.lbu.lbustudent.commons.constants;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    STUDENT_NOT_AVAILABLE("Given student id is not available.", 10000),
    STUDENT_VALIDATION_ERROR("Invalid course details provided.", 10001),
    STUDENT_ALREADY_AVAILABLE_ERROR("Given course is already available.", 10002),

    INTERNAL_ERROR("An error occurred.", 9000),
    INVALID_UUID("Invalid UUID provided.", 9001),
    INVALID_HREF("Invalid href provided.", 9002),

    JWT_TOKEN_SECRET_NOT_AVAILABLE("Token secret is not available.", 6000),
    JWT_TOKEN_EXPIRED("Given token is expired.", 6001),
    JWT_TOKEN_INVALID_SIGNATURE("Invalid token provided.", 6002),
    JWT_TOKEN_NOT_AVAILABLE("Authorization header does not have a token.", 6003),
    JWT_TOKEN_USER_NOT_AVAILABLE("Given User not available.", 6004),
    JWT_TOKEN_USER_MISMATCH("Invalid user access.", 6005),
    JWT_TOKEN_INVALID("Invalid token.", 6006),

    AUTH_SERVICE_USER_UPDATE_ERROR("An error occurred in the auth service while upgrading the user.", 7000);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
