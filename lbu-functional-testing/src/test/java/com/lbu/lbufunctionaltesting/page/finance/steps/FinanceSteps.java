package com.lbu.lbufunctionaltesting.page.finance.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.finance.FinancePage;

@LazyComponent
public class FinanceSteps {

    @LazyAutowired
    FinancePage financePage;

    public FinanceSteps verifyFinances() {
        financePage.verifyFinances();
        return this;
    }

    public FinanceSteps givenIamValidateFinancesArePaid() {
        financePage
                .verifyFinances()
                .verifyFinancesAreInPending();
        return this;
    }

    public FinanceSteps thenIamInitThePay() {
        financePage
                .verifyFinances()
                .verifyFinancesAreInPending()
                .initPaymentFirstInvoice();
        return this;
    }

    public FinanceSteps thenIamInitCancelPay() {
        financePage
                .initCancelSecondInvoice();
        return this;
    }

    public FinanceSteps validatePaymentDone() {
        return this;
    }
}
