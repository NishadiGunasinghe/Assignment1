package com.lbu.lbufinance.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema($schema = "Finance Account Details")
public class AccountDto {
    @Schema($schema = "Account id")
    private String id;
    @Schema($schema = "Account auth user href")
    private String authUserHref;
    @Schema($schema = "Account invoices")
    private List<InvoiceDto> invoiceList;
}
