package com.lbu.lbuauth.services.impl;

import com.lbu.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.models.SecretDetails;
import com.lbu.lbuauth.models.SecretWrapper;
import com.lbu.lbuauth.models.User;
import com.lbu.lbuauth.models.enums.SecretType;
import com.lbu.lbuauth.repositories.SecretDetailRepository;
import com.lbu.lbuauth.repositories.UserRepository;
import com.lbu.lbuauth.services.JwtService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbuauth.commons.constants.ErrorConstants.*;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    Pattern BEARER_PATTERN = Pattern.compile("Bearer\\s+(.*)");
    private final SecretDetailRepository secretDetailRepository;
    private final UserRepository userRepository;

    @Value("${custom.properties.jwt.expiration.hours}")
    private Integer expirationDuration;
    @Value("${custom.properties.jwt.issuer}")
    private String tokenIssuer;

    /**
     * Constructor for JwtServiceImpl.
     * @param secretDetailRepository Repository for managing secret details.
     * @param userRepository Repository for managing user data.
     */
    public JwtServiceImpl(SecretDetailRepository secretDetailRepository, UserRepository userRepository) {
        this.secretDetailRepository = secretDetailRepository;
        this.userRepository = userRepository;
    }

    /**
     * Validates the authentication user by verifying the authenticity of the JWT token.
     * @param authUserId The ID of the authenticated user.
     * @param authToken The JWT token to validate.
     * @return The authenticated user ID if valid.
     * @throws LBUAuthRuntimeException If authentication fails due to token mismatch or invalid token.
     */
    @Override
    public String validateAuthUser(String authUserId, String authToken) {
        Matcher matcher = BEARER_PATTERN.matcher(authToken);
        if (matcher.find()) {
            Claims claims = validateAndGetClaims(matcher.group(1));
            if (authUserId.equals(claims.get(USER_ID).toString())) {
                return authUserId;
            } else {
                throw new LBUAuthRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
            }
        }
        throw new LBUAuthRuntimeException(JWT_TOKEN_INVALID.getErrorMessage(), JWT_TOKEN_INVALID.getErrorCode());
    }

    /**
     * Retrieves authentication details from the JWT token.
     * @param authToken The JWT token.
     * @return Authentication details.
     * @throws LBUAuthRuntimeException If an error occurs during token validation or user retrieval.
     */
    public Authentication getAuthentication(String authToken) {
        try {
            Claims claims = validateAndGetClaims(authToken);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(ROLES).toString());
            grantedAuthorities.add(authority);
            Optional<User> optionalUser = userRepository.findById(claims.get(USER_ID).toString());
            if (optionalUser.isPresent()) {
                UserDetails userDetails = optionalUser.get();
                return new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);
            } else {
                throw new LBUAuthRuntimeException(JWT_TOKEN_USER_NOT_AVAILABLE.getErrorMessage(), JWT_TOKEN_USER_NOT_AVAILABLE.getErrorCode());
            }
        } catch (Exception e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUAuthRuntimeException(INTERNAL_ERROR.getErrorMessage(), e, INTERNAL_ERROR.getErrorCode());
        }
    }

    /**
     * Validates the provided JWT token.
     * @param authToken The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String authToken) {
        return !validateAndGetClaims(authToken).isEmpty();
    }

    /**
     * Generates a JWT token for the given user.
     * @param selectedUser The user for whom the token is generated.
     * @return DTO containing the generated JWT token.
     * @throws LBUAuthRuntimeException If an error occurs during token generation.
     */
    @Override
    public JWTTokenDto generateJwtToken(User selectedUser) {
        try {
            SecretDetails secretDetails = secretDetailRepository.findBySecretType(SecretType.JWT_SECRET);
            if (Objects.isNull(secretDetails)) {
                SecretDetails secret = new SecretDetails();
                secret.setSecretType(SecretType.JWT_SECRET);
                SecretWrapper secretWrapper = generateSecretWrapper();
                secret.setPrivateKey(keyToString(secretWrapper.getPrivateKey()));
                secret.setPublicKey(keyToString(secretWrapper.getPublicKey()));
                secretDetails = secretDetailRepository.save(secret);

            }
            JWTTokenDto jwtTokenDto = new JWTTokenDto();
            Map<String, Object> claims = new HashMap<>();
            claims.put(ROLES, selectedUser.getRoleType().getStringRoleType());
            claims.put(USER_ID, selectedUser.getId());
            claims.put(FIRST_NAME, selectedUser.getFirstName());
            claims.put(LAST_NAME, selectedUser.getLastName());
            jwtTokenDto.setJwtToken(Jwts.builder()
                    .subject(selectedUser.getUsername())
                    .issuer(tokenIssuer)
                    .issuedAt(Date.from(Instant.now()))
                    .encodePayload(true)
                    .id(UUID.randomUUID().toString())
                    .expiration(new Date(System.currentTimeMillis() + (expirationDuration * 60 * 60 * 1000)))
                    .signWith(stringToPrivateKey(secretDetails.getPrivateKey()))
                    .claims(claims)
                    .compact());
            jwtTokenDto.setUserId(selectedUser.getId());
            return jwtTokenDto;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUAuthRuntimeException(INTERNAL_ERROR.getErrorMessage(), e, INTERNAL_ERROR.getErrorCode());
        }
    }

    /**
     * Validates and retrieves claims from the JWT token.
     * @param authToken The JWT token to validate.
     * @return Claims extracted from the token.
     * @throws LBUAuthRuntimeException If an error occurs during token validation or parsing.
     */
    private Claims validateAndGetClaims(String authToken) {
        try {
            SecretDetails secretDetails = secretDetailRepository.findBySecretType(SecretType.JWT_SECRET);
            if (Objects.isNull(secretDetails)) {
                throw new LBUAuthRuntimeException(JWT_TOKEN_SECRET_NOT_AVAILABLE.getErrorMessage(), JWT_TOKEN_SECRET_NOT_AVAILABLE.getErrorCode());
            }
            return (Claims) Jwts.parser()
                    .verifyWith(stringToPublicKey(secretDetails.getPublicKey()))
                    .build()
                    .parse(authToken).getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature", e);
            throw new LBUAuthRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), e, JWT_TOKEN_INVALID_SIGNATURE.getErrorCode());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            throw new LBUAuthRuntimeException(JWT_TOKEN_EXPIRED.getErrorMessage(), e, JWT_TOKEN_EXPIRED.getErrorCode());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new LBUAuthRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), e, JWT_TOKEN_INVALID_SIGNATURE.getErrorCode());
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid", e);
            throw new LBUAuthRuntimeException(JWT_TOKEN_INVALID_SIGNATURE.getErrorMessage(), e, JWT_TOKEN_INVALID_SIGNATURE.getErrorCode());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("An error occurred while generating public key and private key", e);
            throw new LBUAuthRuntimeException(INTERNAL_ERROR.getErrorMessage(), e, INTERNAL_ERROR.getErrorCode());
        }
    }

    /**
     * Converts a string representation of a private key to a PrivateKey object.
     *
     * @param privateKeyString The string representation of the private key.
     * @return PrivateKey object.
     * @throws NoSuchAlgorithmException If the specified algorithm for key generation is invalid.
     * @throws InvalidKeySpecException If the provided key specification is invalid.
     */
    private PrivateKey stringToPrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString.trim())));
    }

    /**
     * Generates a secret key pair for JWT token signing.
     * @return Wrapper containing the generated public and private keys.
     * @throws NoSuchAlgorithmException If the specified key pair generation algorithm is invalid.
     */
    private SecretWrapper generateSecretWrapper() throws NoSuchAlgorithmException {
        // Generating KeyPair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Key size of 2048 bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        SecretWrapper secretWrapper = new SecretWrapper();
        secretWrapper.setPrivateKey(keyPair.getPrivate());
        secretWrapper.setPublicKey(keyPair.getPublic());
        return secretWrapper;
    }

    /**
     * Converts a public/private key to its string representation.
     * @param key The key to be converted.
     * @return String representation of the key.
     */
    private String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Converts a string representation of a public key to a PublicKey object.
     * @param publicKeyString The string representation of the public key.
     * @return PublicKey object.
     * @throws InvalidKeySpecException If the provided key specification is invalid.
     * @throws NoSuchAlgorithmException If the specified algorithm for key generation is invalid.
     */
    private PublicKey stringToPublicKey(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString.trim())));
    }
}
