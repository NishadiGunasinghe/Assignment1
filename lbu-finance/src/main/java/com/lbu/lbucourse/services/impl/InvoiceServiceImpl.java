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

    /**
     * Retrieves all invoices associated with the authenticated user. It utilizes the invoice repository to find all invoices
     * linked to the provided user authentication href.
     *
     * @param authUserHref The authentication href of the user whose invoices are to be retrieved.
     * @return A list of invoices associated with the specified user.
     */
    @Override
    public List<Invoice> getAllInvoicesForUser(String authUserHref) {
        return this.invoiceRepository.findAllByAccount_AuthUserHref(authUserHref);
    }

    /**
     * Cancels an invoice with the specified reference for the authenticated user. It first finds the invoice by its reference,
     * validates the ownership of the invoice, sets its status to CANCELLED, and saves the changes. If the invoice is not found,
     * it throws an LBUFinanceRuntimeException.
     *
     * @param authUserHref The authentication href of the user canceling the invoice.
     * @param reference The reference of the invoice to be canceled.
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
            throw new LBUFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Pays an invoice with the specified reference for the authenticated user. It first finds the invoice by its reference,
     * validates the ownership of the invoice, sets its status to PAID, and saves the changes. If the invoice is not found,
     * it throws an LBUFinanceRuntimeException.
     *
     * @param authUserHref The authentication href of the user paying the invoice.
     * @param reference The reference of the invoice to be paid.
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
            throw new LBUFinanceRuntimeException(INVOICE_NOT_AVAILABLE.getErrorMessage(), INVOICE_NOT_AVAILABLE.getErrorCode());
        }
    }

    /**
     * Validates the ownership of an invoice against the provided user authentication href. If the invoice does not belong to
     * the specified user, it throws an LBUFinanceRuntimeException indicating a JWT token user mismatch.
     *
     * @param invoice The invoice to validate ownership for.
     * @param authUserHref The authentication href of the user to compare ownership against.
     */
    private void validateInvoiceOwnership(Invoice invoice, String authUserHref) {
        if (!invoice.getAccount().getAuthUserHref().equals(authUserHref)) {
            throw new LBUFinanceRuntimeException(JWT_TOKEN_USER_MISMATCH.getErrorMessage(), JWT_TOKEN_USER_MISMATCH.getErrorCode());
        }
    }
}
