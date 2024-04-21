package com.lbu.lbulibrary.controllers;

import com.lbu.lbulibrary.dtos.MessageDto;
import com.lbu.lbulibrary.dtos.StudentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/library/")
public interface LibraryStudentController {

    @PostMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Create Student")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the student",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad student content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> createStudent(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Student")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully found the student",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad book content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<StudentDto> getStudent(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);


    @PostMapping("/student/borrow/{isbn}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Borrow Books")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully borrowed the book",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad borrowed content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> borrowBook(@PathVariable String isbn, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping("/student/return/{isbn}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Return Books")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully returned the book",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad returned content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> returnBook(@PathVariable String isbn, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
