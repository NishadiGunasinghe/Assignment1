package com.lbu.lbulibrary.commons.externalservices.finance.services;

public interface FinanceService {

    void createOrUpdateFinanceAccount(String authUserHref, String token, Double fee, Integer bookPayDuration);
}
