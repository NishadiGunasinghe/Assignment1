package com.lbu.lbustudent.controllers;

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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface StudentController {

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

}
