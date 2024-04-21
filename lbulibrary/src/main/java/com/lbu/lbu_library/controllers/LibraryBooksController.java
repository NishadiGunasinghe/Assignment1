package com.lbu.lbu_library.controllers;

import com.lbu.lbu_library.dtos.BookDto;
import com.lbu.lbu_library.dtos.BookDtos;
import com.lbu.lbu_library.dtos.MessageDto;
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
public interface LibraryBooksController {

    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Books")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the book",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookDto.class))
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
    ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto);

    @GetMapping("/books/{isbn}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Books")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully found the book",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookDto.class))
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
    ResponseEntity<BookDto> getBook(@PathVariable String isbn);

    @GetMapping("/books")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get All Books")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully get all the books",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookDtos.class))
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
    ResponseEntity<BookDtos> getBooks(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PutMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Book")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated the book",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookDto.class))
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
    ResponseEntity<BookDto> updateBook(@RequestBody BookDto bookDto);
}
