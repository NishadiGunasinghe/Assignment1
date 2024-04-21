package com.lbu.lbustudent.configuration;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Service
public class JsonTokenTestService {

    @Value("${custom.properties.jwt.private.key}")
    private String privateKey;
    private static final String USER_ID = "userId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ROLES = "roles";

    public String getJwtToken(String role, String authUserId) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLES, role);
        claims.put(USER_ID, authUserId);
        claims.put(FIRST_NAME, "Test First Name");
        claims.put(LAST_NAME, "Test Last Name");

        return Jwts.builder()
                .subject("test")
                .issuer("lbu-auth")
                .issuedAt(Date.from(Instant.now()))
                .encodePayload(true)
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(stringToPrivateKey(privateKey))
                .claims(claims)
                .compact();
    }

    private PrivateKey stringToPrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString.trim())));
    }
}
