package com.lbu.lbucourse.commons.constants;

import lombok.Getter;

@Getter
public enum ErrorConstants {

    COURSE_NOT_AVAILABLE("Given course id is not available.", 10000),
    COURSE_VALIDATION_ERROR("Invalid course details provided.", 10001),

    INVALID_UUID("Invalid UUID provided.", 9001),
    INVALID_HREF("Invalid href provided.", 9002),
    INTERNAL_ERROR("An error occurred.", 9000);

    private final String errorMessage;
    private final Integer errorCode;

    ErrorConstants(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
