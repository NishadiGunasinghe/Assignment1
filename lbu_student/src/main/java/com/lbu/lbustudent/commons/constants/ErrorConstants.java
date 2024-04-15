package com.lbu.lbustudent.commons.constants;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    STUDENT_NOT_AVAILABLE("Given student id is not available.", 10000),
    STUDENT_VALIDATION_ERROR("Invalid course details provided.", 10001),
    STUDENT_ALREADY_AVAILABLE_ERROR("Given course is already available.", 10002),

    INTERNAL_ERROR("An error occurred.", 9000),
    INVALID_UUID("Invalid UUID provided.", 9001),
    INVALID_HREF("Invalid href provided.", 9002);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
