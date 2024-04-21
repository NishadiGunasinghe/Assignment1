package com.lbu.lbustudent.commons.externalservices.auth.services.impl;

import com.lbu.lbustudent.commons.externalservices.auth.services.AuthService;
import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    public Authentication getAuthentication(String authToken) {
        try {
            Claims claims = this.validateAndGetClaims(authToken);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("roles").toString());
            grantedAuthorities.add(authority);
            return new PreAuthenticatedAuthenticationToken(new User(claims.get("sub").toString(), "", grantedAuthorities), authToken, grantedAuthorities);
        } catch (Exception e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.INTERNAL_ERROR.getErrorMessage(), ErrorConstants.INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    public String validateAuthUserHref(String authUserHref, String authToken) {
        Matcher matcher = this.BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = this.validateAndGetClaims(matcher.group(1));
            String authTokenUserHref = "/auth/user/".concat(claims.get("userId").toString());
            if (authUserHref.equals(authTokenUserHref)) {
                return authTokenUserHref;
            } else {
                throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_USER_MISMATCH.getErrorMessage(), ErrorConstants.JWT_TOKEN_USER_MISMATCH.getErrorCode());
            }
        } else {
            throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_INVALID.getErrorMessage(), ErrorConstants.JWT_TOKEN_INVALID.getErrorCode());
        }
    }

    private Claims validateAndGetClaims(String authToken) {
        try {
            return (Claims) Jwts.parser().verifyWith(this.stringToPublicKey(this.publicKey)).build().parse(authToken).getPayload();
        } catch (MalformedJwtException | SecurityException e) {
            log.error("Invalid JWT signature", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_EXPIRED.getErrorMessage(), ErrorConstants.JWT_TOKEN_EXPIRED.getErrorCode(), e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), ErrorConstants.JWT_TOKEN_INVALID_SIGNATURE.getErrorCode(), e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUStudentsRuntimeException(ErrorConstants.INTERNAL_ERROR.getErrorMessage(), ErrorConstants.INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    public JWTTokenDto updateUserStatus(String authUserHref, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            return this.authRestTemplate.postForObject(authUserHref, requestEntity, JWTTokenDto.class);
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(ErrorConstants.INTERNAL_ERROR.getErrorMessage(), ErrorConstants.INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    private PublicKey stringToPublicKey(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString.trim())));
    }
}
