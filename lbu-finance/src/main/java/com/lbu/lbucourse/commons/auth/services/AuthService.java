package com.lbu.lbucourse.commons.auth.services;

import org.springframework.security.core.Authentication;

public interface AuthService {
    String USER_ID = "userId";

    String AUTH_USER_HREF = "/auth/user/";
    String ROLES = "roles";
    boolean validateToken(String token);

    Authentication getAuthentication(String token);

    String validateAuthUserHref(String authUserHref, String token);
    String validateAuthUserHref(String token);
}
