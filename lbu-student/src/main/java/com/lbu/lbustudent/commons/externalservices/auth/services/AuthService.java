package com.lbu.lbustudent.commons.externalservices.auth.services;

import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import org.springframework.security.core.Authentication;

public interface AuthService {

    String AUTH_USER_HREF = "/auth/user/";
    String USER_ID = "userId";
    String ROLES = "roles";
    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    JWTTokenDto updateUserStatus(String authUserHref, String token);

    String validateAuthUserHref(String authUserHref, String authToken);
}
