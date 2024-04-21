package com.lbu.lbuauth.controllers;

import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.dtos.LoginDto;
import com.lbu.lbuauth.dtos.MessageDto;
import com.lbu.lbuauth.dtos.UserDto;
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

@Tag(name = "User Auth Controller")
@RequestMapping("/auth")
public interface UserLoginController {

    @PostMapping("/login")
    @Operation(summary = "Login User")
    @ApiResponse(
            responseCode = "200",
            description = "successfully login the user",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = JWTTokenDto.class))
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
    ResponseEntity<JWTTokenDto> login(@RequestBody LoginDto loginDto);

    @GetMapping("/token/{userId}")
    @Operation(summary = "Activate User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully re send activate user",
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
    ResponseEntity<MessageDto> reSendActivateUserAccount(@PathVariable String userId);

    @PostMapping("/user/{userId}")
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
    ResponseEntity<JWTTokenDto> updateUserRole(@PathVariable String userId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @GetMapping("/activation/{token}")
    @Operation(summary = "Activate User")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully activated the user",
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
    ResponseEntity<MessageDto> activateUserAccount(@PathVariable String token);


    @PostMapping("/validate")
    @Operation(summary = "Validate Token")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully activated the user",
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
    ResponseEntity<MessageDto> validateToken(@RequestBody JWTTokenDto jwtTokenDto);
}
