package com.lbu.lbu_library.service;

import com.lbu.lbu_library.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionsByAuthUserHref(String authUserHref);
}
