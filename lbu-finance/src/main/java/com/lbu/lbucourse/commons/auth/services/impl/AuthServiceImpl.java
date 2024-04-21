package com.lbu.lbucourse.commons.auth.services.impl;

import com.lbu.lbucourse.commons.auth.services.AuthService;
import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

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

import static com.lbu.lbucourse.commons.constants.ErrorConstants.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${custom.properties.jwt.public.key}")
    private String publicKey;

    Pattern BEARER_PATTERN = Pattern.compile("Bearer\\s+(.*)");

    @Override
    public boolean validateToken(String token) {
        return !validateAndGetClaims(token).isEmpty();
    }

    @Override
    public Authentication getAuthentication(String authToken) {
        try {
            Claims claims = validateAndGetClaims(authToken);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(ROLES).toString());
            grantedAuthorities.add(authority);
            return new PreAuthenticatedAuthenticationToken(new User(claims.get("sub").toString(), "", grantedAuthorities), authToken, grantedAuthorities);
        } catch (Exception e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    @Override
    public String validateAuthUserHref(String authUserHref, String authToken) {
        Matcher matcher = BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = validateAndGetClaims(matcher.group(1));
            String authTokenUserHref = AUTH_USER_HREF.concat(claims.get(USER_ID).toString());
            if (authUserHref.equals(authTokenUserHref)) {
                return authTokenUserHref;
            } else {
                throw new LBUFinanceRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
            }
        }
        throw new LBUFinanceRuntimeException(JWT_TOKEN_INVALID.getErrorMessage(), JWT_TOKEN_INVALID.getErrorCode());
    }

    @Override
    public String validateAuthUserHref(String authToken) {
        Matcher matcher = BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = validateAndGetClaims(matcher.group(1));
            return AUTH_USER_HREF.concat(claims.get(USER_ID).toString());
        }
        throw new LBUFinanceRuntimeException(JWT_TOKEN_INVALID.getErrorMessage(), JWT_TOKEN_INVALID.getErrorCode());
    }

    private PublicKey stringToPublicKey(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString.trim())));
    }

    private Claims validateAndGetClaims(String authToken) {
        try {
            return (Claims) Jwts.parser()
                    .verifyWith(stringToPublicKey(publicKey))
                    .build()
                    .parse(authToken).getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature", e);
            throw new LBUFinanceRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            throw new LBUFinanceRuntimeException(JWT_TOKEN_EXPIRED.getErrorMessage(), JWT_TOKEN_EXPIRED.getErrorCode(), e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new LBUFinanceRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid", e);
            throw new LBUFinanceRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
