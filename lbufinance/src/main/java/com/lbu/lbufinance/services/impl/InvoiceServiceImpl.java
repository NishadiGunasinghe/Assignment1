package com.lbu.lbufinance.services.impl;

import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import com.lbu.lbufinance.models.Invoice;
import com.lbu.lbufinance.models.Status;
import com.lbu.lbufinance.repositories.InvoiceRepository;
import com.lbu.lbufinance.services.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.lbu.lbufinance.commons.constants.ErrorConstants.INVOICE_NOT_AVAILABLE;
import static com.lbu.lbufinance.commons.constants.ErrorConstants.JWT_TOKEN_USER_MISMATCH;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    /**
     * Constructs an InvoiceServiceImpl with the provided InvoiceRepository.
     *
     * @param invoiceRepository The repository for managing invoice data.
     */
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Retrieves all invoices associated with a specific user.
     *
     * @param authUserHref The href of the authentication user.
     * @return A list of invoices associated with the user.
     */
    @Override
    public List<Invoice> getAllInvoicesForUser(String authUserHref) {
        return this.invoiceRepository.findAllByAccount_AuthUserHref(authUserHref);
    }

    /**
     * Cancels an invoice for the specified user and reference.
     *
     * @param authUserHref The href of the authentication user.
     * @param reference    The reference of the invoice to cancel.
     * @throws LbuFinanceRuntimeException If the invoice is not available or if there's a mismatch in ownership.
     */
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
            throw new LbuFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Pays an invoice for the specified user and reference.
     *
     * @param authUserHref The href of the authentication user.
     * @param reference    The reference of the invoice to pay.
     * @throws LbuFinanceRuntimeException If the invoice is not available or if there's a mismatch in ownership.
     */
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
            throw new LbuFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Validates if the invoice belongs to the specified user.
     *
     * @param invoice      The invoice to validate ownership.
     * @param authUserHref The href of the authentication user.
     * @throws LbuFinanceRuntimeException If there's a mismatch in ownership.
     */
    private void validateInvoiceOwnership(Invoice invoice, String authUserHref) {
        if (!invoice.getAccount().getAuthUserHref().equals(authUserHref)) {
            throw new LbuFinanceRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
        }
    }

}
