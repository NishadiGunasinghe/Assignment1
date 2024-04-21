package com.lbu.lbustudent.commons.externalservices.library.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LibraryRestTemplateConfiguration {


    @Value("${custom.properties.library.baseurl}")
    private String libraryBaseUrl;

    private final RestTemplateBuilder restTemplateBuilder;

    public LibraryRestTemplateConfiguration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean("libraryRestTemplate")
    public RestTemplate createAuthRestTemplate() {
        return restTemplateBuilder
                .rootUri(libraryBaseUrl)
                .build();
    }

}
