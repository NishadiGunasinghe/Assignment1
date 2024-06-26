package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.commons.auth.services.AuthService;
import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.controllers.InvoiceController;
import com.lbu.lbucourse.dtos.FinanceInvoiceDto;
import com.lbu.lbucourse.dtos.FinanceInvoiceDtos;
import com.lbu.lbucourse.dtos.MessageDto;
import com.lbu.lbucourse.models.Invoice;
import com.lbu.lbucourse.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.INTERNAL_ERROR;
import static com.lbu.lbucourse.commons.constants.SuccessConstants.INVOICE_CANCEL_SUCCESS;
import static com.lbu.lbucourse.commons.constants.SuccessConstants.INVOICE_PAY_SUCCESS;

@Slf4j
@RestController
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;
    private final AuthService authService;

    private final ModelMapper modelMapper;

    InvoiceControllerImpl(InvoiceService invoiceService,
                          AuthService authService,
                          ModelMapper modelMapper) {
        this.invoiceService = invoiceService;
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves finance details for the authenticated user identified by the provided token. It first validates the user's
     * authentication token to get their unique href. Then, it fetches all invoices associated with that user. If any model
     * conversion error occurs during the mapping of invoices to DTOs, it logs the error and throws an LBUFinanceRuntimeException
     * with an internal error message and code. Otherwise, it returns a ResponseEntity containing the FinanceInvoiceDtos.
     *
     * @param token The authentication token of the user.
     * @return ResponseEntity containing FinanceInvoiceDtos.
     */
    @Override
    public ResponseEntity<FinanceInvoiceDtos> getFinanceDetailsForUser(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        FinanceInvoiceDtos invoiceDtos = new FinanceInvoiceDtos();
        List<Invoice> invoiceList = invoiceService.getAllInvoicesForUser(authUserHref);
        try {
            invoiceDtos.setInvoices(invoiceList.stream()
                    .map(invoice -> modelMapper.map(invoice, FinanceInvoiceDto.class)).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Model conversion error", e);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        return ResponseEntity.ok(invoiceDtos);
    }

    /**
     * Cancels the invoice identified by the provided reference for the authenticated user identified by the provided token.
     * It validates the user's authentication token to get their unique href, then cancels the invoice using the InvoiceService.
     * Upon successful cancellation, it constructs a MessageDto indicating the success and returns it within a ResponseEntity.
     *
     * @param reference The reference of the invoice to cancel.
     * @param token     The authentication token of the user.
     * @return ResponseEntity containing MessageDto indicating the cancellation success.
     */
    @Override
    public ResponseEntity<MessageDto> cancelInvoice(String reference, String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        invoiceService.cancelInvoice(authUserHref, reference);
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(INVOICE_CANCEL_SUCCESS.getErrorCode());
        messageDto.setMessage(INVOICE_CANCEL_SUCCESS.getSuccessMessage());
        return ResponseEntity.ok(messageDto);
    }

    /**
     * Pays the invoice identified by the provided reference for the authenticated user identified by the provided token.
     * It validates the user's authentication token to get their unique href, then pays the invoice using the InvoiceService.
     * Upon successful payment, it constructs a MessageDto indicating the success and returns it within a ResponseEntity.
     *
     * @param reference The reference of the invoice to pay.
     * @param token     The authentication token of the user.
     * @return ResponseEntity containing MessageDto indicating the payment success.
     */
    @Override
    public ResponseEntity<MessageDto> payInvoice(String reference, String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        invoiceService.payInvoice(authUserHref, reference);
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(INVOICE_PAY_SUCCESS.getErrorCode());
        messageDto.setMessage(INVOICE_PAY_SUCCESS.getSuccessMessage());
        return ResponseEntity.ok(messageDto);
    }
}