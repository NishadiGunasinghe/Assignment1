package com.lbu.lbu_library.commons.externalservices.auth.services;

import org.springframework.security.core.Authentication;

public interface AuthService {

    String AUTH_USER_HREF = "/auth/user/";
    String USER_ID = "userId";
    String ROLES = "roles";

    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    String validateAuthUserHref(String authUserHref, String authToken);

    String validateAuthUserHref(String token);
}
