package com.lbu.lbu_library.service.impl;

import com.lbu.lbu_library.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbu_library.models.Transaction;
import com.lbu.lbu_library.repositories.TransactionRepository;
import com.lbu.lbu_library.service.BookFineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * Constructs a BookFineServiceImpl with the provided TransactionRepository and FinanceService.
     *
     * @param transactionRepository The repository for transactions.
     * @param financeService        The service for finance operations.
     */
    public BookFineServiceImpl(TransactionRepository transactionRepository, FinanceService financeService) {
        this.transactionRepository = transactionRepository;
        this.financeService = financeService;
    }

    /**
     * Checks for book fines asynchronously.
     *
     * @param token        The authentication token.
     * @param authUserHref The href of the authentication user.
     */
    public void checkForBookFines(String token, String authUserHref) {
        Thread bookFineCheckThread = new Thread(() -> {
            try {
                List<Transaction> transactions = transactionRepository.findAllDateReturnedIsNull(bookReturnTimeInSeconds);
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
