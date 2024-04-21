package com.lbu.lbucourse.services.impl;

import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.models.Invoice;
import com.lbu.lbucourse.models.Status;
import com.lbu.lbucourse.repositories.InvoiceRepository;
import com.lbu.lbucourse.services.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.INVOICE_NOT_AVAILABLE;
import static com.lbu.lbucourse.commons.constants.ErrorConstants.JWT_TOKEN_USER_MISMATCH;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Invoice> getAllInvoicesForUser(String authUserHref) {
        return this.invoiceRepository.findAllByAccount_AuthUserHref(authUserHref);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void cancelInvoice(String authUserHref, String reference) {
        Optional<Invoice> invoiceByReference = invoiceRepository.findInvoiceByReference(reference);
        if (invoiceByReference.isPresent()) {
            Invoice invoice = invoiceByReference.get();
            validateInvoiceOwnership(invoice, authUserHref);
            invoice.setStatus(Status.CANCELLED);
            invoiceRepository.save(invoice);
        } else {
            throw new LBUFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void payInvoice(String authUserHref, String reference) {
        Optional<Invoice> invoiceByReference = invoiceRepository.findInvoiceByReference(reference);
        if (invoiceByReference.isPresent()) {
            Invoice invoice = invoiceByReference.get();
            validateInvoiceOwnership(invoice, authUserHref);
            invoice.setStatus(Status.PAID);
            invoiceRepository.save(invoice);
        } else {
            throw new LBUFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    private void validateInvoiceOwnership(Invoice invoice, String authUserHref) {
        if (!invoice.getAccount().getAuthUserHref().equals(authUserHref)) {
            throw new LBUFinanceRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
        }
    }
}
