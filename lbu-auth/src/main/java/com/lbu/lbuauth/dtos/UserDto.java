package com.lbu.lbuauth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User Data Transfer Object")
public class UserDto {
    @Schema(description = "User Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;
    @Schema(description = "User Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;
    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "First  Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @Schema(description = "Last Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
