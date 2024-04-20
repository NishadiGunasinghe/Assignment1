package com.lbu.lbu_library.commons.externalservices.auth.services.impl;

import com.lbu.lbu_library.commons.externalservices.auth.services.AuthService;
import com.lbu.lbu_library.commons.exceptions.LBULibraryRuntimeException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbu_library.commons.constants.ErrorConstants.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${custom.properties.jwt.public.key}")
    private String publicKey;
    Pattern BEARER_PATTERN = Pattern.compile("Bearer\\s+(.*)");
    @Qualifier("authRestTemplate")
    private final RestTemplate authRestTemplate;

    public AuthServiceImpl(RestTemplate authRestTemplate) {
        this.authRestTemplate = authRestTemplate;
    }

    public boolean validateToken(String token) {
        return !this.validateAndGetClaims(token).isEmpty();
    }

    /**
     * Retrieves authentication information from the JWT token.
     *
     * @param authToken The JWT token.
     * @return An Authentication object containing user authentication information.
     * @throws LBULibraryRuntimeException If an error occurs during authentication.
     */
    public Authentication getAuthentication(String authToken) {
        try {
            Claims claims = this.validateAndGetClaims(authToken);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("roles").toString());
            grantedAuthorities.add(authority);
            return new PreAuthenticatedAuthenticationToken(new User(claims.get("sub").toString(), "", grantedAuthorities), authToken, grantedAuthorities);
        } catch (Exception e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Validates the authenticity of the authenticated user's href.
     *
     * @param authUserHref The href of the authenticated user.
     * @param authToken    The JWT token.
     * @return The validated authentication user href.
     * @throws LBULibraryRuntimeException If the token is invalid or if the user href doesn't match with the token.
     */
    public String validateAuthUserHref(String authUserHref, String authToken) {
        Matcher matcher = this.BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = this.validateAndGetClaims(matcher.group(1));
            String authTokenUserHref = "/auth/user/".concat(claims.get("userId").toString());
            if (authUserHref.equals(authTokenUserHref)) {
                return authTokenUserHref;
            } else {
                throw new LBULibraryRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
            }
        } else {
            throw new LBULibraryRuntimeException(JWT_TOKEN_INVALID.getErrorMessage(), JWT_TOKEN_INVALID.getErrorCode());
        }
    }

    /**
     * Validates the authenticity of the authenticated user's href.
     *
     * @param authToken The JWT token.
     * @return The validated authentication user href.
     * @throws LBULibraryRuntimeException If the token is invalid.
     */
    @Override
    public String validateAuthUserHref(String authToken) {
        Matcher matcher = BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = validateAndGetClaims(matcher.group(1));
            return AUTH_USER_HREF.concat(claims.get(USER_ID).toString());
        }
        throw new LBULibraryRuntimeException(JWT_TOKEN_INVALID.getErrorMessage(), JWT_TOKEN_INVALID.getErrorCode());
    }

    /**
     * Validates the JWT token and retrieves its claims.
     *
     * @param authToken The JWT token to validate and retrieve claims from.
     * @return The claims extracted from the JWT token.
     * @throws LBULibraryRuntimeException If the token is invalid or if an error occurs during validation.
     */
    private Claims validateAndGetClaims(String authToken) {
        try {
            return (Claims) Jwts.parser().verifyWith(this.stringToPublicKey(this.publicKey)).build().parse(authToken).getPayload();
        } catch (MalformedJwtException | SecurityException e) {
            log.error("Invalid JWT signature", e);
            throw new LBULibraryRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            throw new LBULibraryRuntimeException(JWT_TOKEN_EXPIRED.getErrorMessage(), JWT_TOKEN_EXPIRED.getErrorCode(), e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new LBULibraryRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid", e);
            throw new LBULibraryRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Converts a public key string to PublicKey object.
     *
     * @param publicKeyString The public key string.
     * @return The PublicKey object.
     * @throws InvalidKeySpecException If the provided key specification is invalid.
     * @throws NoSuchAlgorithmException If the specified algorithm is not available.
     */
    private PublicKey stringToPublicKey(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString.trim())));
    }

}
