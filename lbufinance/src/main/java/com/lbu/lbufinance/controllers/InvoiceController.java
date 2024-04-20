package com.lbu.lbufinance.controllers;

import com.lbu.lbufinance.dtos.InvoiceDto;
import com.lbu.lbufinance.dtos.InvoiceDtos;
import com.lbu.lbufinance.dtos.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/finance")
public interface InvoiceController {

    @GetMapping("/invoice")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get Invoices")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully get all invoices",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvoiceDto.class))
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
    ResponseEntity<InvoiceDtos> getFinanceDetailsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @DeleteMapping("/invoice/{reference}/cancel")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully cancel the invoice",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad invoice content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> cancelInvoice(@PathVariable String reference,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PutMapping("/invoice/{reference}/pay")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully cancel the invoice",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad invoice content",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageDto.class))
    )
    ResponseEntity<MessageDto> payInvoice(@PathVariable String reference,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
