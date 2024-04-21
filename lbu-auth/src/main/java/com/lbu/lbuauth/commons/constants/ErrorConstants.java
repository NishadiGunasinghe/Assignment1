package com.lbu.lbuauth.commons.constants;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    ACCOUNT_NOT_ENABLE_ERROR("The account is not activated. Please activate it before logging in.", 4000),
    ACCOUNT_CREDENTIAL_EXPIRED_ERROR("The account credentials have expired. Please reset your credentials.", 4001),
    ACCOUNT_LOCKED_ERROR("The account credentials are locked. Please wait 24 hours before attempting to log in again.", 4002),
    ACCOUNT_PASSWORD_INVALID_ERROR("The username or password provided is invalid. Please try again with the correct username and password.", 4003),
    ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR("The provided username does not exist. Please create a new account or use a valid username.", 4004),
    ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR("The provided user id does not exist.", 4005),
    ACCOUNT_ALREADY_AVAILABLE_ERROR("The provided username or email already available.", 4006),

    ACCOUNT_ACTIVATED_ERROR("Given account already activated.", 5000),
    ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR("Existing account activation link still usable please use it to activate.", 5001),
    ACCOUNT_ACTIVATION_TOKEN_EXPIRED("Given token is expired.", 5002),
    ACCOUNT_ACTIVATION_TOKEN_INVALID("Given token is invalid.", 5003),

    JWT_TOKEN_SECRET_NOT_AVAILABLE("Token secret is not available.", 6000),
    JWT_TOKEN_EXPIRED("Given token is expired.", 6001),
    JWT_TOKEN_INVALID_SIGNATURE("Invalid token provided.", 6002),
    JWT_TOKEN_NOT_AVAILABLE("Authorization header does not have a token.", 6003),
    JWT_TOKEN_USER_NOT_AVAILABLE("Given User not available.", 6004),
    JWT_TOKEN_USER_MISMATCH("Invalid user access.", 6005),
    JWT_TOKEN_INVALID("Invalid token.", 6006),

    INVALID_CREDENTIALS("Invalid credentials provided.", 7000),

    EMAIL_SEND_FAILED("Email sending failed.", 8000),

    INTERNAL_ERROR("An error occurred.", 9000);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
