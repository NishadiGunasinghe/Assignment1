package com.lbu.lbufinance.services;

import com.lbu.lbufinance.models.Account;

public interface AccountService {
    Account createFinanceAccount(Account account);

    Account getAccountDetailsForAuthHref(String authUserHref);
}
