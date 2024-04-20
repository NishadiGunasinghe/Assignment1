package com.lbu.lbufinance.commons.constants;

import lombok.Getter;

@Getter
public enum SuccessConstants {

    INVOICE_CANCEL_SUCCESS("Given invoice successfully cancelled.", 2000),
    INVOICE_PAY_SUCCESS("Given invoice successfully payed.", 2001);

    private final String successMessage;
    private final Integer errorCode;

    SuccessConstants(String successMessage, Integer errorCode) {
        this.successMessage = successMessage;
        this.errorCode = errorCode;
    }
}
