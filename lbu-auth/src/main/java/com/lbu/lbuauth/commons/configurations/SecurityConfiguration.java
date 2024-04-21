package com.lbu.lbuauth.commons.configurations;

import com.lbu.lbuauth.commons.filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(this::createPermissionForEndpoints)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void createPermissionForEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.
                                                      AuthorizationManagerRequestMatcherRegistry matcherRegistry) {
        try {
            // Permit access to specific endpoints without authentication
            matcherRegistry
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    .requestMatchers(getRequestMatcherForMethodAndPattern(HttpMethod.POST, "/auth/login")).permitAll()
                    .requestMatchers(getRequestMatcherForMethodAndPattern(HttpMethod.POST, "/auth/user")).permitAll()
                    .requestMatchers(getRequestMatcherForMethodAndPattern(HttpMethod.GET, "/auth/activation/*")).permitAll()
                    .requestMatchers(getRequestMatcherForMethodAndPattern(HttpMethod.GET, "/auth/token/*")).permitAll()
                    .requestMatchers(getRequestMatcherForMethodAndPattern(HttpMethod.POST, "/auth/validate")).permitAll()
                    .anyRequest().authenticated();
        } catch (Exception e) {
            log.error("An error occurred while authorizing the request", e);
        }
    }

    private RequestMatcher getRequestMatcherForMethodAndPattern(HttpMethod method, String pattern) {
        return new AntPathRequestMatcher(pattern, method.name());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
