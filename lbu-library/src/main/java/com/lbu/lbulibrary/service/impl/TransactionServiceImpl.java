package com.lbu.lbulibrary.service.impl;

import com.lbu.lbulibrary.models.Transaction;
import com.lbu.lbulibrary.repositories.TransactionRepository;
import com.lbu.lbulibrary.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getTransactionsByAuthUserHref(String authUserHref) {
        return transactionRepository.findAllByStudent_AuthUserHrefAndDateReturnedIsNull(authUserHref);
    }
}
