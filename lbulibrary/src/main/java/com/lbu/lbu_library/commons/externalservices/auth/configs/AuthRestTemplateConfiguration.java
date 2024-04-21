package com.lbu.lbu_library.commons.externalservices.auth.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthRestTemplateConfiguration {


    @Value("${custom.properties.auth.baseurl}")
    private String authBaseUrl;

    private final RestTemplateBuilder restTemplateBuilder;

    public AuthRestTemplateConfiguration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean("authRestTemplate")
    public RestTemplate createAuthRestTemplate() {
        return restTemplateBuilder
                .rootUri(authBaseUrl)
                .build();
    }

}
