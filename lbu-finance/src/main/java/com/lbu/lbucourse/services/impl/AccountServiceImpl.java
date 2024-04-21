package com.lbu.lbucourse.services.impl;

import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.models.Account;
import com.lbu.lbucourse.models.Status;
import com.lbu.lbucourse.repositories.AccountRepository;
import com.lbu.lbucourse.repositories.InvoiceRepository;
import com.lbu.lbucourse.services.AccountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.ACCOUNT_NOT_AVAILABLE;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;

    public AccountServiceImpl(AccountRepository accountRepository, InvoiceRepository invoiceRepository) {
        this.accountRepository = accountRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public Account createFinanceAccount(Account account) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(account.getAuthUserHref());
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found a existing account since adding the invoice to {}", account.getAuthUserHref());
            account.getInvoiceList().forEach(invoice -> {
                invoice.setStatus(Status.OUTSTANDING);
                invoice.populateReference();
                invoice.setAccount(existingAccount);
            });
            invoiceRepository.saveAllAndFlush(account.getInvoiceList());
            return accountRepository.findById(existingAccount.getId()).get();
        } else {
            log.info("Create new finance account for {}", account.getAuthUserHref());
            Account savedAccount = accountRepository.save(account);
            account.getInvoiceList().forEach(invoice -> {
                invoice.setStatus(Status.OUTSTANDING);
                invoice.populateReference();
                invoice.setAccount(savedAccount);
            });
            invoiceRepository.saveAllAndFlush(account.getInvoiceList());
            return accountRepository.findById(savedAccount.getId()).get();
        }
    }

    @Override
    public Account getAccountDetailsForAuthHref(String authUserHref) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(authUserHref);
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found a existing account since adding the invoice to {}", existingAccount.getAuthUserHref());
            return existingAccount;
        } else {
            throw new LBUFinanceRuntimeException(ACCOUNT_NOT_AVAILABLE.getErrorMessage(), ACCOUNT_NOT_AVAILABLE.getErrorCode());
        }
    }
}
