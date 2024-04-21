package com.lbu.lbulibrary.commons.externalservices.finance.services.impl;

import com.lbu.lbulibrary.commons.constants.ErrorConstants;
import com.lbu.lbulibrary.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbulibrary.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbulibrary.dtos.finance.FinanceAccountDto;
import com.lbu.lbulibrary.dtos.finance.FinanceInvoiceDto;
import com.lbu.lbulibrary.dtos.finance.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {

    private final RestTemplate financeRestTemplate;

    public FinanceServiceImpl(RestTemplate financeRestTemplate) {
        this.financeRestTemplate = financeRestTemplate;
    }

    @Override
    public void createOrUpdateFinanceAccount(String authUserHref, String token, Double fee, Integer bookPayDuration) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            FinanceAccountDto financeAccountDto = new FinanceAccountDto();
            financeAccountDto.setAuthUserHref(authUserHref);
            FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
            financeInvoiceDto.setAmount(fee);
            financeInvoiceDto.setType(Type.LIBRARY_FINE);
            financeInvoiceDto.setDueDate(LocalDate.now().plusDays(bookPayDuration));
            financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
            HttpEntity<FinanceAccountDto> requestEntity = new HttpEntity<>(financeAccountDto, headers);
            FinanceAccountDto financeAccount = financeRestTemplate.postForObject("/finance/account", requestEntity, FinanceAccountDto.class);
            log.info("Finance created {}", financeAccount);
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(ErrorConstants.INTERNAL_ERROR.getErrorMessage(), ErrorConstants.INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
