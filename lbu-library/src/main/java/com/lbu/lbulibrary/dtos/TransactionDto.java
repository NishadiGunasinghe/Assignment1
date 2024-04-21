package com.lbu.lbulibrary.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Schema(description = "book transaction")
public class TransactionDto {
    @Schema(description = "book details")
    private BookDto book;
    @Schema(description = "book borrowed time stamp")
    private Timestamp dateBorrowed;
    @Schema(description = "book returned time stamp")
    private Timestamp dateReturned;
    @Schema(description = "Transaction updated time stamp")
    private Timestamp createdTimestamp;
    @Schema(description = "Transaction updated time stamp")
    private Timestamp updatedTimestamp;
}
