package com.example.lbuauth.commons.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // Creating a configuration source for URL-based CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Creating a CORS configuration object
        CorsConfiguration config = new CorsConfiguration();

        // Allowing credentials (e.g., cookies) to be sent in cross-origin requests
        config.setAllowCredentials(false);

        // Allowing requests from any origin
        config.addAllowedOrigin("*");

        // Allowing all headers to be included in cross-origin requests
        config.addAllowedHeader("*");

        // Allowing all HTTP methods (GET, POST, PUT, DELETE, etc.) in cross-origin requests
        config.addAllowedMethod("*");

        // Registering the CORS configuration for all endpoints
        source.registerCorsConfiguration("/**", config);

        // Creating and returning a CORS filter with the configured source
        return new CorsFilter(source);
    }

}

