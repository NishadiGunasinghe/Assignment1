package com.lbu.lbu_library.commons.externalservices.finance.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FinanceRestTemplateConfiguration {


    @Value("${custom.properties.finance.baseurl}")
    private String financeBaseUrl;

    private final RestTemplateBuilder restTemplateBuilder;

    public FinanceRestTemplateConfiguration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean("financeRestTemplate")
    public RestTemplate createAuthRestTemplate() {
        return restTemplateBuilder
                .rootUri(financeBaseUrl)
                .build();
    }

}
