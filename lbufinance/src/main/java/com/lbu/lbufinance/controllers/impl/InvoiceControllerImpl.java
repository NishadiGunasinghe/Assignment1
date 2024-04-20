package com.lbu.lbufinance.controllers.impl;

import com.lbu.lbufinance.commons.auth.service.AuthService;
import com.lbu.lbufinance.controllers.InvoiceController;
import com.lbu.lbufinance.dtos.InvoiceDto;
import com.lbu.lbufinance.dtos.InvoiceDtos;
import com.lbu.lbufinance.dtos.MessageDto;
import com.lbu.lbufinance.models.Invoice;
import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import com.lbu.lbufinance.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.lbu.lbufinance.commons.constants.ErrorConstants.INTERNAL_ERROR;
import static com.lbu.lbufinance.commons.constants.SuccessConstants.INVOICE_CANCEL_SUCCESS;
import static com.lbu.lbufinance.commons.constants.SuccessConstants.INVOICE_PAY_SUCCESS;

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
     * Retrieves finance details for a user based on the provided authentication token.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing FinanceInvoiceDtos with the user's finance details.
     */
    @Override
    public ResponseEntity<InvoiceDtos> getFinanceDetailsForUser(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        InvoiceDtos invoiceDtos = new InvoiceDtos();
        List<Invoice> invoiceList = invoiceService.getAllInvoicesForUser(authUserHref);
        try {
            invoiceDtos.setInvoices(invoiceList.stream()
                    .map(invoice -> modelMapper.map(invoice, InvoiceDto.class)).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Model conversion error", e);
            throw new LbuFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        return ResponseEntity.ok(invoiceDtos);
    }

    /**
     * Cancels an invoice for the user identified by the provided authentication token.
     *
     * @param reference The reference of the invoice to cancel.
     * @param token     The authentication token.
     * @return ResponseEntity containing MessageDto with the cancellation status message.
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
     * Pays an invoice for the user identified by the provided authentication token.
     *
     * @param reference The reference of the invoice to pay.
     * @param token     The authentication token.
     * @return ResponseEntity containing MessageDto with the payment status message.
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
