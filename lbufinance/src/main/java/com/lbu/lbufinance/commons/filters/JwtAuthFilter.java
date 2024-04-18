package com.lbu.lbufinance.commons.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbu.lbufinance.commons.auth.service.AuthService;
import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import com.lbu.lbufinance.dtos.MessageDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lbu.lbufinance.commons.constants.ErrorConstants.JWT_TOKEN_NOT_AVAILABLE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final ObjectMapper objectMapper;
    Pattern BEARER_PATTERN = Pattern.compile("Bearer\\s+(.*)");

    /**
     * This method is the entry point for the filter. It intercepts incoming requests,
     * extracts the authorization header, processes it, and delegates further processing to the filter chain.
     *
     * @param request     The incoming HttpServletRequest.
     * @param response    The HttpServletResponse.
     * @param filterChain The filter chain for further processing.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        try {
            if (authHeader != null) {
                processAuthHeader(authHeader);
            }
            filterChain.doFilter(request, response);
        } catch (LbuFinanceRuntimeException ex) {
            generateErrorMessage(response, ex);
        }
    }

    /**
     * Processes the authorization header to extract and validate the JWT token.
     *
     * @param authHeader The authorization header containing the JWT token.
     * @throws LbuFinanceRuntimeException If the JWT token is not available or if an error occurs during validation.
     */
    private void processAuthHeader(String authHeader) {
        String token;
        Matcher matcher = BEARER_PATTERN.matcher(authHeader);
        if (matcher.find()) {
            token = matcher.group(1);
        } else {
            throw new LbuFinanceRuntimeException(JWT_TOKEN_NOT_AVAILABLE.getErrorMessage(), JWT_TOKEN_NOT_AVAILABLE.getErrorCode());
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (authService.validateToken(token)) {
                PreAuthenticatedAuthenticationToken authentication = (PreAuthenticatedAuthenticationToken) authService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    /**
     * Generates an error message response based on the provided exception.
     *
     * @param response The HttpServletResponse to which the error message will be written.
     * @param ex       The exception containing error information.
     * @throws IOException If an I/O error occurs.
     */
    private void generateErrorMessage(HttpServletResponse response, LbuFinanceRuntimeException ex) throws IOException {
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(ex.getCode());
        messageDto.setMessage(ex.getMessage());
        response.setStatus(BAD_REQUEST.value());
        response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        response.getWriter().write(convertObjectToJson(messageDto));
    }

    /**
     * Converts a Java object to JSON string.
     *
     * @param object The object to be converted to JSON.
     * @return The JSON representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return objectMapper.writeValueAsString(object);
    }

}
