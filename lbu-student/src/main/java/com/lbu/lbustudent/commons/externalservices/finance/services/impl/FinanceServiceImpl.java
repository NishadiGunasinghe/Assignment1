package com.lbu.lbustudent.commons.externalservices.finance.services.impl;

import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbustudent.dtos.course.CourseDto;
import com.lbu.lbustudent.dtos.finance.FinanceAccountDto;
import com.lbu.lbustudent.dtos.finance.FinanceInvoiceDto;
import com.lbu.lbustudent.dtos.finance.Type;
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
    public void createOrUpdateFinanceAccount(CourseDto courseDto, String authUserHref, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            FinanceAccountDto financeAccountDto = new FinanceAccountDto();
            financeAccountDto.setAuthUserHref(authUserHref);
            FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
            financeInvoiceDto.setAmount(courseDto.getFees().doubleValue());
            financeInvoiceDto.setType(Type.TUITION_FEES);
            financeInvoiceDto.setDueDate(LocalDate.now().plusDays(courseDto.getDurationInDays()));
            financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
            HttpEntity<FinanceAccountDto> requestEntity = new HttpEntity<>(financeAccountDto, headers);
            FinanceAccountDto financeAccount = financeRestTemplate.postForObject("/finance/account", requestEntity, FinanceAccountDto.class);
            log.info("Finance created {}", financeAccount);
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorMessage(), ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorCode(), e);
        }
    }
}
