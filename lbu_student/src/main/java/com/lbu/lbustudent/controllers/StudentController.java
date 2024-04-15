package com.lbu.lbustudent.controllers;

import com.lbu.lbustudent.commons.exeptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.dtos.MessageDto;
import com.lbu.lbustudent.dtos.StudentDto;
import com.lbu.lbustudent.dtos.StudentEnrolmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.*;
import static com.lbu.lbustudent.commons.constants.ErrorConstants.INVALID_UUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface StudentController {

    Pattern COURSE_ID_PATTERN = Pattern.compile("^/courses/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");
    Pattern AUTH_ID_PATTERN = Pattern.compile("^/auth/user/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$");


    /**
     * Endpoint to create student enrolment.
     * This endpoint is accessible to users with roles: STUDENT, ADMIN, or GENERAL_USER.
     *
     * @param studentEnrolmentDto The DTO containing student enrolment information.
     * @param token               The authorization token in the request header.
     * @return ResponseEntity containing the created Student DTO.
     */
    @PostMapping("/student/enrolment")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
    @Operation(summary = "Create Student")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the student",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad user content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<StudentDto> createStudentEnrolment(@RequestBody StudentEnrolmentDto studentEnrolmentDto,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    /**
     * Handles POST requests for updating student information.
     * Users must have the role 'STUDENT' or 'ADMIN' to access this endpoint.
     *
     * @param studentDto The updated student data to be processed.
     * @param token      The authorization token provided in the request header.
     * @return           ResponseEntity containing the updated StudentDto upon successful update.
     *                   Returns status code 200 and updated student data in JSON format.
     *                   If the request body is invalid, returns status code 400 and a MessageDto.
     *                   If there's an internal server error, returns status code 500 and a MessageDto.
     */
    @PostMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Update Student")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully update the student",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad user content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<StudentDto> updateStudent(@RequestBody StudentDto studentDto,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    /**
     * Retrieves student information.
     * Requires the user to have either 'STUDENT' or 'ADMIN' role.
     *
     * @param authUserHref Optional parameter representing the authentication user href.
     * @param studentId   Optional parameter representing the student's ID.
     * @param token       Header containing the authorization token.
     * @return ResponseEntity containing StudentDto if successful.
     *         Returns 400 Bad Request if the user content is invalid, with MessageDto.
     *         Returns 500 Internal Server Error if there's an issue on the server side, with MessageDto.
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Student")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully get the student",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad user content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<StudentDto> getStudent(@RequestParam(required = false) String authUserHref,
                                          @RequestParam(required = false) String studentId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    /**
     * Validates the href string and extracts the ID for a course or authentication.
     *
     * @param href The href string to be validated.
     */
    default void validateHrefAndGetId(String href) {
        // Matcher for extracting course ID from href
        Matcher courseMatcher = COURSE_ID_PATTERN.matcher(href);
        // Matcher for extracting authentication ID from href
        Matcher authMatcher = AUTH_ID_PATTERN.matcher(href);

        // If course ID found, validate it
        if (courseMatcher.find()) {
            validateId(courseMatcher.group(1));
        }
        // If authentication ID found, validate it
        else if (authMatcher.find()) {
            validateId(authMatcher.group(1));
        }
        // If neither course ID nor authentication ID found, throw runtime exception
        else {
            throw new LBUStudentsRuntimeException(INVALID_HREF.getErrorMessage(), INVALID_HREF.getErrorCode());
        }
    }

    /**
     * Validates the format of the given course or authentication ID.
     *
     * @param courseId The ID to be validated.
     */
    default void validateId(String courseId) {
        try {
            // Attempt to create a UUID from the string
            UUID.fromString(courseId);
        } catch (IllegalArgumentException e) {
            // If invalid format, throw runtime exception
            throw new LBUStudentsRuntimeException(INVALID_UUID.getErrorMessage(), INVALID_UUID.getErrorCode(), e);
        }
    }

    /**
     * Validates the completeness of the provided StudentDto.
     *
     * @param studentDto The StudentDto object to be validated.
     */
    default void validateStudentDto(StudentDto studentDto) {
        // Validate the href and extract ID
        validateHrefAndGetId(studentDto.getAuthUserHref());

        // Check for null fields in the StudentDto object
        if (Objects.isNull(studentDto.getAddress()) ||
                Objects.isNull(studentDto.getEmergencyContact()) ||
                Objects.isNull(studentDto.getDateOfBirth()) ||
                Objects.isNull(studentDto.getPhoneContact())
        ) {
            // If any required field is null, throw runtime exception
            throw new LBUStudentsRuntimeException(STUDENT_VALIDATION_ERROR.getErrorMessage(), STUDENT_VALIDATION_ERROR.getErrorCode());
        }
    }

}
