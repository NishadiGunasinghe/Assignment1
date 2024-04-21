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

    /**
     * Creates a finance account based on the provided account details. It first checks if an account already exists
     * for the given authentication user href. If an account exists, it associates the provided invoices with the existing
     * account, updates their status to OUTSTANDING, populates their reference, and saves them. If no account exists,
     * it creates a new finance account, associates the invoices with it, updates their status to OUTSTANDING, populates
     * their reference, and saves them. This method is annotated with @Transactional to ensure atomicity, rolling back
     * changes in case of any Exception.
     *
     * @param account The account details to create or update.
     * @return The created or updated account.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Account createFinanceAccount(Account account) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(account.getAuthUserHref());
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found an existing account since adding the invoice to {}", account);
            account.getInvoiceList().forEach(invoice -> {
                invoice.setStatus(Status.OUTSTANDING);
                invoice.populateReference();
                invoice.setAccount(existingAccount);
            });
            invoiceRepository.saveAllAndFlush(account.getInvoiceList());
            return accountRepository.findById(existingAccount.getId()).get();
        } else {
            log.info("Create a new finance account for {}", account);
            account.getInvoiceList().forEach(invoice -> {
                invoice.setStatus(Status.OUTSTANDING);
                invoice.populateReference();
                invoice.setAccount(account);
            });
            Account savedAccount = accountRepository.save(account);
            invoiceRepository.saveAllAndFlush(account.getInvoiceList());
            return accountRepository.findById(savedAccount.getId()).get();
        }
    }

    /**
     * Retrieves the account details for the specified authentication user href. If an account is found, it returns
     * the account details. If no account is found, it throws an LBUFinanceRuntimeException indicating that the account
     * is not available.
     *
     * @param authUserHref The authentication user href to retrieve the account details for.
     * @return The account details for the specified authentication user href.
     * @throws LBUFinanceRuntimeException If the account is not available.
     */
    @Override
    public Account getAccountDetailsForAuthHref(String authUserHref) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(authUserHref);
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found an existing account since adding the invoice to {}", existingAccount.getAuthUserHref());
            return existingAccount;
        } else {
            throw new LBUFinanceRuntimeException(ACCOUNT_NOT_AVAILABLE.getErrorMessage(), ACCOUNT_NOT_AVAILABLE.getErrorCode());
        }
    }
}
