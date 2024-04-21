package com.lbu.lbucourse.controllers;

import com.lbu.lbucourse.commons.exceptions.LBUCourcesRuntimeException;
import com.lbu.lbucourse.dtos.CourseDto;
import com.lbu.lbucourse.dtos.CourseDtos;
import com.lbu.lbucourse.dtos.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface CoursesController {

    String REST_COURSES_COURSE_ID = "/courses/{courseId}";
    String REST_COURSES = "/courses";
    String REST_COURSES_LIST = "/courses/list";
    Pattern COURSE_ID_PATTERN = Pattern.compile("^/courses/\\d+$");

    @PostMapping(REST_COURSES_LIST)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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

    @DeleteMapping(REST_COURSES_COURSE_ID)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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
    ResponseEntity<MessageDto> deleteCourse(@PathVariable String courseId);

    @PostMapping(REST_COURSES)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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

    @PutMapping(REST_COURSES)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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
    ResponseEntity<CourseDto> updateCourse(@RequestBody CourseDto courseDto);

    @GetMapping(REST_COURSES)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
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

    default void validateCoursesWithId(CourseDto courseDto) {
        Matcher matcher = COURSE_ID_PATTERN.matcher(courseDto.getIdHref());
        if (matcher.find()) {
            validateCourseId(matcher.group(0));
        } else {
            throw new LBUCourcesRuntimeException(INVALID_HREF.getErrorMessage(), INVALID_HREF.getErrorCode());
        }
        validateCourses(courseDto);
    }

    default void validateCourses(CourseDto courseDto) {
        if (Objects.isNull(courseDto.getTitle())
                || Objects.isNull(courseDto.getFees())
                || Objects.isNull(courseDto.getDescription())
                || Objects.isNull(courseDto.getDurationInDays())) {
            throw new LBUCourcesRuntimeException(COURSE_VALIDATION_ERROR.getErrorMessage(), COURSE_VALIDATION_ERROR.getErrorCode());
        }
    }

    default void validateCourseId(String courseId) {
        try {
            // Attempt to create a UUID from the string
            UUID.fromString(courseId);
        } catch (IllegalArgumentException e) {
            throw new LBUCourcesRuntimeException(INVALID_UUID.getErrorMessage(), INVALID_UUID.getErrorCode(), e);
        }
    }
}
