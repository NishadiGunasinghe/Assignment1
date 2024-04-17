package com.example.lbuauth.dtos;

import lombok.Data;

@Data
public class JWTTokenDto {
    private String jwtToken;
    private String userId;
}
