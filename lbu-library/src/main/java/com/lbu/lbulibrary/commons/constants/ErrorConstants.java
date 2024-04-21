package com.lbu.lbulibrary.commons.constants;

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

    STUDENT_NOT_AVAILABLE("Given student id is not available.", 10000),
    BOOK_NOT_AVAILABLE("Given book id is not available.", 10001),
    BOOK_ALREADY_BORROWED("Given book is already borrowed please return it before get new.", 10002),
    MAX_BOOK_ALREADY_BORROWED("Given book is already borrowed by everyone please check after few days.", 10003),
    BOOK_NOT_BORROWED("Given book is not borrowed by the student.", 10004),
    BOOK_ALREADY_RETURNED("Given book is already returned.", 10005),
    BOOK__NOT_HAVING_REQUIRED("please add all the required details for the book.", 10006),

    INTERNAL_ERROR("An error occurred.", 9000);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
