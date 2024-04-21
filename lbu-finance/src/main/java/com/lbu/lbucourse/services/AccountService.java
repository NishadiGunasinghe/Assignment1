package com.lbu.lbucourse.services;

import com.lbu.lbucourse.models.Account;

public interface AccountService {
    Account createFinanceAccount(Account account);

    Account getAccountDetailsForAuthHref(String authUserHref);
}
