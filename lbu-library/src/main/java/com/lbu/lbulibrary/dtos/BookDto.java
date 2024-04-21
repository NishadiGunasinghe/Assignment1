package com.lbu.lbulibrary.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Schema(description = "Book Detail")
public class BookDto {
    @Schema(description = "Book id")
    private String id;
    @Schema(description = "Book isbn")
    private String isbn;
    @Schema(description = "Book title")
    private String title;
    @Schema(description = "Book author")
    private String author;
    @Schema(description = "Book year")
    private Integer yearOfPublished;
    @Schema(description = "Book copies")
    private Integer copies;
    @Schema(description = "Book created time stamp")
    private Timestamp createdTimestamp;
    @Schema(description = "Book updated time stamp")
    private Timestamp updatedTimestamp;
    @Schema(description = "Book already borrowed by user")
    private Boolean isBorrowed;
}
