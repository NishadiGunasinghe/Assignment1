package com.lbu.lbuauth.controllers;

import com.lbu.lbuauth.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "User Controller")
@RequestMapping("/auth")
public interface UserController {

    @PostMapping("/user")
    @Operation(summary = "Create User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the user",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))
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
    ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto);

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('GENERAL_USER')")
    @Operation(summary = "Get User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully found the user",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not available",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<UserDto> getUser(@PathVariable String userId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Delete User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully deleted the user",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not available",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> deleteUser(@PathVariable String userId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token);


    @PutMapping("/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Update User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated the user",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad user content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
