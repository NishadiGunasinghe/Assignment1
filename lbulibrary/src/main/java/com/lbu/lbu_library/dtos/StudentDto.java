package com.lbu.lbu_library.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Schema(description = "Student Details")
public class StudentDto {
    @Schema(description = "Student id")
    private String id;
    @Schema(description = "Student auth user id")
    private String authUserHref;
    @Schema(description = "Student borrowed books")
    private List<TransactionDto> borrowedBooks;
    @Schema(description = "Student updated time stamp")
    private Timestamp createdTimestamp;
    @Schema(description = "Student updated time stamp")
    private Timestamp updatedTimestamp;
}
