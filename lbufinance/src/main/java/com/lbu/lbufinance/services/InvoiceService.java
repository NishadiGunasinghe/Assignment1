package com.lbu.lbufinance.services;

import com.lbu.lbufinance.models.Invoice;

import java.util.List;

public interface InvoiceService {
    List<Invoice> getAllInvoicesForUser(String authUserHref);

    void cancelInvoice(String authUserHref, String reference);

    void payInvoice(String authUserHref, String reference);
}
