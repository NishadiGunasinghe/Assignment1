package com.lbu.lbustudent.service;

import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.models.Student;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.*;

public interface StudentService {

    Pattern COURSE_ID_PATTERN = Pattern.compile("^/courses/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");
    Pattern AUTH_ID_PATTERN = Pattern.compile("^/auth/user/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");

    Student createStudentEnrolment(String authUserHref, String courseHref, String token);

    Student updateStudentDetails(Student student, String authToken);

    Student getStudentDetailsFromAuthIdOrStudentId(String authUserHref, String studentId, String authToken);

    default void validateStudent(Student student) {
        validateHrefAndGetId(student.getAuthUserHref());
        if (Objects.isNull(student.getAddress()) ||
                Objects.isNull(student.getEmergencyContact()) ||
                Objects.isNull(student.getDateOfBirth()) ||
                Objects.isNull(student.getPhoneContact())
        ) {
            throw new LBUStudentsRuntimeException(STUDENT_VALIDATION_ERROR.getErrorMessage(), STUDENT_VALIDATION_ERROR.getErrorCode());
        }
    }

    default void validateHrefAndGetId(String href) {
        Matcher courseMatcher = COURSE_ID_PATTERN.matcher(href);
        Matcher authMatcher = AUTH_ID_PATTERN.matcher(href);
        if (courseMatcher.find()) {
            validateId(courseMatcher.group(1));
        } else if (authMatcher.find()) {
            validateId(authMatcher.group(1));
        } else {
            throw new LBUStudentsRuntimeException(INVALID_HREF.getErrorMessage(), INVALID_HREF.getErrorCode());
        }
    }

    default void validateId(String courseId) {
        try {
            // Attempt to create a UUID from the string
            UUID.fromString(courseId);
        } catch (IllegalArgumentException e) {
            throw new LBUStudentsRuntimeException(INVALID_UUID.getErrorMessage(), INVALID_UUID.getErrorCode(), e);
        }
    }
}
