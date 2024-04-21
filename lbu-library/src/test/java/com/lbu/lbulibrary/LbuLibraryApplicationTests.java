package com.lbu.lbulibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbu.lbulibrary.commons.externalservices.auth.services.AuthService;
import com.lbu.lbulibrary.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbulibrary.configuration.TestDataConfiguration;
import com.lbu.lbulibrary.repositories.BookRepository;
import com.lbu.lbulibrary.repositories.StudentRepository;
import com.lbu.lbulibrary.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {LbuLibraryApplication.class, TestDataConfiguration.class})
public abstract class LbuLibraryApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected FinanceService financeService;

    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected BookRepository bookRepository;


    protected String withAuthenticationButInvalidToken() {
        String token = "afasasfasfasfas";
        when(authService.validateToken(token)).thenReturn(Boolean.FALSE);
        return token;
    }

    protected String withAuthenticationButInvalidAccessLevel() {
        String token = "afasasfasfasfas";
        when(authService.validateToken(token)).thenReturn(Boolean.TRUE);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        grantedAuthorities.add(authority);
        when(authService.getAuthentication(token)).thenReturn(new PreAuthenticatedAuthenticationToken(new User("test", "", grantedAuthorities), token, grantedAuthorities));
        return token;
    }

    protected String withAuthenticationWithStudent() {
        String token = "afasasfasfasfas";
        when(authService.validateToken(token)).thenReturn(Boolean.TRUE);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_STUDENT");
        grantedAuthorities.add(authority);
        when(authService.getAuthentication(token)).thenReturn(new PreAuthenticatedAuthenticationToken(new User("test", "", grantedAuthorities), token, grantedAuthorities));
        return token;
    }

    protected String withAuthenticationWithAdmin() {
        String token = "afasasfasfasfas";
        when(authService.validateToken(token)).thenReturn(Boolean.TRUE);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        grantedAuthorities.add(authority);
        when(authService.getAuthentication(token)).thenReturn(new PreAuthenticatedAuthenticationToken(new User("test", "", grantedAuthorities), token, grantedAuthorities));
        return token;
    }

    protected static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void testWithoutHeaders(Object dto, String url, HttpMethod httpMethod) throws Exception {
        if (httpMethod.equals(HttpMethod.POST)) {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(dto)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        } else if (httpMethod.equals(HttpMethod.GET)) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    protected void testInvalidAuthenticationToken(Object dto, String url, HttpMethod httpMethod) throws Exception {
        String token = withAuthenticationButInvalidToken();
        if (httpMethod.equals(HttpMethod.POST)) {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(asJsonString(dto)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        } else if (httpMethod.equals(HttpMethod.GET)) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    protected void testInvalidAccessLevel(Object dto, String url, HttpMethod httpMethod) throws Exception {
        String token = withAuthenticationButInvalidAccessLevel();
        if (httpMethod.equals(HttpMethod.POST)) {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(asJsonString(dto)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        } else if (httpMethod.equals(HttpMethod.GET)) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

}
