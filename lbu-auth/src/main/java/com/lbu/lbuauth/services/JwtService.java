package com.lbu.lbuauth.services;

import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.models.User;
import org.springframework.security.core.Authentication;

public interface JwtService {

    String USER_ID = "userId";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String ROLES = "roles";

    JWTTokenDto generateJwtToken(User selectedUser);

    Authentication getAuthentication(String token);

    boolean validateToken(String token);

    String validateAuthUser(String authUserId, String authToken);
}
