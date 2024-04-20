package com.lbu.lbufinance.dtos;

import com.lbu.lbufinance.models.Status;
import com.lbu.lbufinance.models.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema($schema = "Finance Account Invoice Details")
public class InvoiceDto {
    @Schema($schema = "Invoice Id")
    private String id;
    @Schema($schema = "Invoice Reference Id")
    private String reference;
    @Schema($schema = "Invoice Amount")
    private Double amount;
    @Schema($schema = "Invoice Pay Due Date")
    private LocalDate dueDate;
    @Schema($schema = "Invoice Type")
    private Type type;
    @Schema($schema = "Invoice Pay Status")
    private Status status;
}
