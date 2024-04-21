package com.lbu.lbustudent.commons.externalservices.library.service.impl;

import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.library.service.LibraryService;
import com.lbu.lbustudent.dtos.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LibraryServiceImpl implements LibraryService {
    private final RestTemplate libraryRestTemplate;

    public LibraryServiceImpl(RestTemplate libraryRestTemplate) {
        this.libraryRestTemplate = libraryRestTemplate;
    }

    @Override
    public boolean createLibraryAccount(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<MessageDto> messageDto = libraryRestTemplate.exchange("/library/student", HttpMethod.POST, requestEntity, MessageDto.class);
            log.info("Finance created {}", messageDto.getBody());
            return true;
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(ErrorConstants.LIBRARY_SERVICE_GET_ERROR.getErrorMessage(), ErrorConstants.LIBRARY_SERVICE_GET_ERROR.getErrorCode(), e);
        }
    }
}
