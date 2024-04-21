package com.lbu.lbulibrary.service.impl;

import com.lbu.lbulibrary.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbulibrary.models.Transaction;
import com.lbu.lbulibrary.repositories.TransactionRepository;
import com.lbu.lbulibrary.service.BookFineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookFineServiceImpl implements BookFineService {

    @Value("${custom.properties.seconds.bookreturn}")
    private Long bookReturnTimeInSeconds;

    @Value("${custom.properties.fee.bookreturn}")
    private Double bookReturnFee;

    @Value("${custom.properties.feepayduration.bookreturn}")
    private Integer bookReturnDuration;
    private final TransactionRepository transactionRepository;
    private final FinanceService financeService;

    public BookFineServiceImpl(TransactionRepository transactionRepository, FinanceService financeService) {
        this.transactionRepository = transactionRepository;
        this.financeService = financeService;
    }


    /**
     Checks for fines associated with overdue book returns for a specified user.
     This method creates a new thread to asynchronously check for overdue book transactions
     in the database. It retrieves all transactions where the return date is null and matches
     the authenticated user. If any such transactions are found, it iterates through them,
     logging each transaction ID and initiating a fine for the associated student via the
     finance service. If an exception occurs during the process, it is caught and logged.
     @param token The authentication token for the user.
     @param authUserHref The href of the authenticated user.
     */
    public void checkForBookFines(String token, String authUserHref) {
        Thread bookFineCheckThread = new Thread(() -> {
            try {
                List<Transaction> transactions = transactionRepository.findAllDateReturnedIsNull(bookReturnTimeInSeconds, authUserHref);
                if (!transactions.isEmpty()) {
                    log.info("checking for student who was not returned books {}", transactions.size());
                    for (Transaction transaction : transactions) {
                        log.info("Need to fine the student for the transaction id {}", transaction.getId());
                        financeService.createOrUpdateFinanceAccount(authUserHref, token, bookReturnFee, bookReturnDuration);
                    }
                }
            } catch (Exception e) {
                log.error("An error occurred while calling the finance service", e);
            }
        });
        bookFineCheckThread.start();
    }
}
