package com.lbu.lbucourse.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Message Data Transfer Object")
public class MessageDto {
    @Schema(description = "Message")
    private String message;

    @Schema(description = "Code")
    private Integer code;
}
