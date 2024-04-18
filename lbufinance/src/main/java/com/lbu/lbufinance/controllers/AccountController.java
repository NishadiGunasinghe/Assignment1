package com.lbu.lbufinance.controllers;

import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import com.lbu.lbufinance.dtos.AccountDto;
import com.lbu.lbufinance.dtos.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.lbu.lbufinance.commons.constants.ErrorConstants.ACCOUNT_NOT_VALID_AVAILABLE;

@RequestMapping("/finance")
public interface AccountController {

    @GetMapping("/account")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Finance Account")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the finance account",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountDto.class))
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
    ResponseEntity<AccountDto> getFinanceDetailsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping("/account")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Create Finance Account")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully created the finance account",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountDto.class))
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
    ResponseEntity<AccountDto> createFinanceAccount(@RequestBody AccountDto accountDto,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    default void validateFinanceRequest(AccountDto accountDto) {
        if (Objects.isNull(accountDto.getAuthUserHref())) {
            throw new LbuFinanceRuntimeException(ACCOUNT_NOT_VALID_AVAILABLE.getErrorMessage(), ACCOUNT_NOT_VALID_AVAILABLE.getErrorCode());
        }
    }
}
