package com.lbu.lbucourse.controllers;

import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.dtos.FinanceAccountDto;
import com.lbu.lbucourse.dtos.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.ACCOUNT_NOT_VALID_AVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/finance")
public interface AccountController {

    @GetMapping("/account")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Finance Account")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the finance account",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = FinanceAccountDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad finance account content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<FinanceAccountDto> getFinanceDetailsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping("/account")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Create Finance Account")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the finance account",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = FinanceAccountDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad finance account content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<FinanceAccountDto> createFinanceAccount(@RequestBody FinanceAccountDto accountDto,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    default void validateFinanceRequest(FinanceAccountDto accountDto) {
        if (Objects.isNull(accountDto.getAuthUserHref())) {
            throw new LBUFinanceRuntimeException(ACCOUNT_NOT_VALID_AVAILABLE.getErrorMessage(), ACCOUNT_NOT_VALID_AVAILABLE.getErrorCode());
        }
    }
}
