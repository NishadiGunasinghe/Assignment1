package com.lbu.lbu_library.service.impl;

import com.lbu.lbu_library.models.Transaction;
import com.lbu.lbu_library.repositories.TransactionRepository;
import com.lbu.lbu_library.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Initializes TransactionServiceImpl with the provided TransactionRepository.
     *
     * @param transactionRepository The repository for transactions.
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Retrieves a list of transactions associated with the provided authentication user href.
     *
     * @param authUserHref The href of the authentication user.
     * @return A list of transactions.
     */
    @Override
    public List<Transaction> getTransactionsByAuthUserHref(String authUserHref) {
        return transactionRepository.findAllByStudent_AuthUserHrefAndDateReturnedIsNull(authUserHref);
    }

}
