package com.example.lbuauth.commons.filters;

import com.example.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.example.lbuauth.dtos.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.lbuauth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.lbuauth.commons.constants.ErrorConstants.JWT_TOKEN_NOT_AVAILABLE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // JwtService for token validation and authentication
    private final JwtService jwtService;
    // ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper;
    // Regular expression pattern to extract the JWT token from the Authorization header
    Pattern BEARER_PATTERN = Pattern.compile("Bearer\\s+(.*)");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract Authorization header from the HTTP request
        String authHeader = request.getHeader(AUTHORIZATION);
        try {
            if (authHeader != null) {
                // Process the Authorization header
                processAuthHeader(authHeader);
            }
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (LBUAuthRuntimeException ex) {
            // Handle any authentication runtime exceptions
            generateErrorMessage(response, ex);
        }
    }

    // Method to process the Authorization header and validate the JWT token
    private void processAuthHeader(String authHeader) {
        String token;
        // Extract the token from the Authorization header
        Matcher matcher = BEARER_PATTERN.matcher(authHeader);
        if (matcher.find()) {
            token = matcher.group(1);
        } else {
            // If token is not available, throw an exception
            throw new LBUAuthRuntimeException(JWT_TOKEN_NOT_AVAILABLE.getErrorMessage(), JWT_TOKEN_NOT_AVAILABLE.getErrorCode());
        }
        // Validate the token and set the authentication if not already set
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    // Method to generate error message for authentication exceptions
    private void generateErrorMessage(HttpServletResponse response, LBUAuthRuntimeException ex) throws IOException {
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(ex.getCode());
        messageDto.setMessage(ex.getMessage());
        // Set HTTP status code and content type for the response
        response.setStatus(BAD_REQUEST.value());
        response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        // Write the error message JSON to the response
        response.getWriter().write(convertObjectToJson(messageDto));
    }

    // Method to convert an object to JSON string
    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        // Serialize the object to JSON using ObjectMapper
        return objectMapper.writeValueAsString(object);
    }
}
