package com.lbu.lbuauth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JWT Token Data Transfer Object")
public class JWTTokenDto {
    @Schema(description = "Generated JWT Token")
    private String jwtToken;
    @Schema(description = "User Id")
    private String userId;
}
