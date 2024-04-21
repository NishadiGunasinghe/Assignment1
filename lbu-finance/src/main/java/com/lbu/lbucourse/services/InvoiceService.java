package com.lbu.lbucourse.services;

import com.lbu.lbucourse.models.Invoice;

import java.util.List;

public interface InvoiceService {
    List<Invoice> getAllInvoicesForUser(String authUserHref);

    void cancelInvoice(String authUserHref, String reference);

    void payInvoice(String authUserHref, String reference);
}
