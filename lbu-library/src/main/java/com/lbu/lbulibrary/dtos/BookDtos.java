package com.lbu.lbulibrary.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Book Details")
public class BookDtos {

    @Schema(description = "Book Details")
    List<BookDto> books;

}
