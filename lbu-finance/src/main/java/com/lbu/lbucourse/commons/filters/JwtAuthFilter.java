package com.lbu.lbucourse.commons.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbu.lbucourse.commons.auth.services.AuthService;
import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.dtos.MessageDto;
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

import static com.lbu.lbucourse.commons.constants.ErrorConstants.JWT_TOKEN_NOT_AVAILABLE;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        try {
            if (authHeader != null) {
                processAuthHeader(authHeader);
            }
            filterChain.doFilter(request, response);
        } catch (LBUFinanceRuntimeException ex) {
            generateErrorMessage(response, ex);
        }
    }

    private void processAuthHeader(String authHeader) {
        String token;
        Matcher matcher = BEARER_PATTERN.matcher(authHeader);
        if (matcher.find()) {
            token = matcher.group(1);
        } else {
            throw new LBUFinanceRuntimeException(JWT_TOKEN_NOT_AVAILABLE.getErrorMessage(), JWT_TOKEN_NOT_AVAILABLE.getErrorCode());
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (authService.validateToken(token)) {
                PreAuthenticatedAuthenticationToken authentication = (PreAuthenticatedAuthenticationToken) authService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void generateErrorMessage(HttpServletResponse response, LBUFinanceRuntimeException ex) throws IOException {
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(ex.getCode());
        messageDto.setMessage(ex.getMessage());
        response.setStatus(BAD_REQUEST.value());
        response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        response.getWriter().write(convertObjectToJson(messageDto));
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return objectMapper.writeValueAsString(object);
    }
}
