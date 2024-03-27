package com.lbu.lbucourse.controllers;

import com.lbu.lbucourse.dtos.CourseDto;
import com.lbu.lbucourse.dtos.CourseDtos;
import com.lbu.lbucourse.dtos.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.lbu.lbucourse.commons.exceptions.LBUCourseRuntimeException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.*;
import static com.lbu.lbucourse.commons.constants.ErrorConstants.INVALID_UUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface CourseController {

    String REST_COURSES_COURSE_ID = "/courses/{courseId}";
    String REST_COURSES = "/courses";
    String REST_COURSES_LIST = "/courses/list";


    @GetMapping(REST_COURSES_LIST)
    @Operation(summary = "Create Courses")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the course",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
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
    ResponseEntity<CourseDtos> getCourses(@RequestBody List<String> courseIds);

    @GetMapping(REST_COURSES_COURSE_ID)
    @Operation(summary = "Create Courses")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the course",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
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
    ResponseEntity<CourseDto> getCourse(@PathVariable String courseId);


    @PostMapping(REST_COURSES)
    @Operation(summary = "Create Courses")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the course",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseDto.class))
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
    ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto);

    @GetMapping(REST_COURSES)
    @Operation(summary = "Create Courses")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the course",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CourseDtos.class))
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
    ResponseEntity<CourseDtos> getCourses();

    default void validateCourses(CourseDto courseDto) {
        if (Objects.isNull(courseDto.getTitle())
                || Objects.isNull(courseDto.getFees())
                || Objects.isNull(courseDto.getDescription())
                || Objects.isNull(courseDto.getDurationInDays())) {
            throw new LBUCourseRuntimeException(COURSE_VALIDATION_ERROR.getErrorMessage(), COURSE_VALIDATION_ERROR.getErrorCode());
        }
    }

    default void validateCourseId(String courseId) {
        try {
            // Attempt to create a UUID from the string
            UUID.fromString(courseId);
        } catch (IllegalArgumentException e) {
            throw new LBUCourseRuntimeException(INVALID_UUID.getErrorMessage(), INVALID_UUID.getErrorCode(), e);
        }
    }
}
