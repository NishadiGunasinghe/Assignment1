package com.lbu.lbufinance.services.impl;

import com.lbu.lbufinance.models.Account;
import com.lbu.lbufinance.models.Status;
import com.lbu.lbufinance.repositories.AccountRepository;
import com.lbu.lbufinance.repositories.InvoiceRepository;
import com.lbu.lbufinance.services.AccountService;
import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.lbu.lbufinance.commons.constants.ErrorConstants.ACCOUNT_NOT_AVAILABLE;


@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Constructor for AccountServiceImpl.
     *
     * @param accountRepository The repository for managing account data.
     * @param invoiceRepository The repository for managing invoice data.
     */
    public AccountServiceImpl(AccountRepository accountRepository, InvoiceRepository invoiceRepository) {
        this.accountRepository = accountRepository;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Creates a finance account for the provided account details.
     * If an account already exists for the given user, adds invoices to the existing account.
     *
     * @param account The account details to be created or updated.
     * @return The created or updated account object.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Account createFinanceAccount(Account account) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(account.getAuthUserHref());
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found an existing account since adding the invoice to {}", account.getAuthUserHref());
            account.getInvoiceList().forEach(invoice -> {
                invoice.setStatus(Status.OUTSTANDING);
                invoice.populateReference();
                invoice.setAccount(existingAccount);
            });
            invoiceRepository.saveAllAndFlush(account.getInvoiceList());
            return accountRepository.findById(existingAccount.getId()).get();
        } else {
            log.info("Creating a new finance account for {}", account.getAuthUserHref());
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

    /**
     * Retrieves the account details for the provided authentication user href.
     *
     * @param authUserHref The href of the authentication user.
     * @return The account details corresponding to the provided authUserHref.
     * @throws LbuFinanceRuntimeException If the account details are not available.
     */
    @Override
    public Account getAccountDetailsForAuthHref(String authUserHref) {
        Optional<Account> optionalAccount = accountRepository.findAccountByAuthUserHref(authUserHref);
        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            log.info("Found an existing account since adding the invoice to {}", existingAccount.getAuthUserHref());
            return existingAccount;
        } else {
            throw new LbuFinanceRuntimeException(ACCOUNT_NOT_AVAILABLE.getErrorMessage(), ACCOUNT_NOT_AVAILABLE.getErrorCode());
        }
    }

}
