package com.lbu.lbulibrary.service;

import com.lbu.lbulibrary.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionsByAuthUserHref(String authUserHref);
}
