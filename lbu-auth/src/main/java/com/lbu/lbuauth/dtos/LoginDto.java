package com.lbu.lbuauth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login Data Transfer Object")
public class LoginDto {
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;
    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
