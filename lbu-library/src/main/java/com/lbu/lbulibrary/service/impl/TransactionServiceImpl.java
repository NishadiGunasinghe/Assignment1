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


    /**
     Retrieves a list of transactions associated with the authenticated user's href. It queries the transaction repository
     to find all transactions where the student's AuthUserHref matches the provided authUserHref and the date returned
     is null, indicating ongoing transactions. Returns a list of Transaction objects matching the criteria.

     @param authUserHref The href of the authenticated user whose transactions are to be retrieved.
     @return A list of Transaction objects associated with the authenticated user's href and ongoing transactions.
     */
    @Override
    public List<Transaction> getTransactionsByAuthUserHref(String authUserHref) {
        return transactionRepository.findAllByStudent_AuthUserHrefAndDateReturnedIsNull(authUserHref);
    }
}
