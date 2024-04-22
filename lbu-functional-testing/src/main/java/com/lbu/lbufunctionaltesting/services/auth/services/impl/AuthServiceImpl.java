package com.lbu.lbufunctionaltesting.services.auth.services.impl;

import com.lbu.lbufunctionaltesting.services.auth.services.AuthService;
import com.lbu.lbufunctionaltesting.services.auth.services.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Qualifier("authRestTemplate")
    private final RestTemplate authRestTemplate;

    public AuthServiceImpl(RestTemplate authRestTemplate) {
        this.authRestTemplate = authRestTemplate;
    }

    public void activateUser(String activationToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<MessageDto> exchange = this.authRestTemplate.exchange("/auth/activation/" + activationToken, HttpMethod.GET, requestEntity, MessageDto.class);
            log.info("Account activated {}", exchange.getBody());
        } catch (Exception e) {
            log.error("An error occurred while activating the user", e);
        }
    }
}
