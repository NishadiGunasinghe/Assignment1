package com.lbu.lbufunctionaltesting.tests.graduation;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.finance.steps.FinanceSteps;
import com.lbu.lbufunctionaltesting.page.graduation.steps.GraduationSteps;
import com.lbu.lbufunctionaltesting.page.library.steps.LibrarySteps;
import com.lbu.lbufunctionaltesting.tests.StudentLevelBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class GraduationTest extends StudentLevelBaseTest {

    @LazyAutowired
    GraduationSteps graduationSteps;

    @Value("${test-data.library.borrow.isbn}")
    String borrowIsbn;

    @LazyAutowired
    LibrarySteps librarySteps;

    @LazyAutowired
    FinanceSteps financeSteps;

    @Test
    public void WhenStudentHasFinancesToPay_ThenCheckGraduation_ReturnALetterToCompletePayments() throws InterruptedException {
        checkFinance();

        homeSteps
                .givenIAmAtGraduationPage();

        graduationSteps
                .verifyNeedCompletionOfPaymentBeforeCertificate();
    }

    @Test
    public void WhenStudentHasNoFinancesToPay_ThenCheckGraduation_ReturnACertificate() throws InterruptedException {
        checkFinance();

        financeSteps
                .givenIamValidateFinancesArePaid()
                .thenIamInitThePay()
                .validatePaymentDone();
        Thread.sleep(1000);
        financeSteps
                .thenIamInitCancelPay();

        homeSteps
                .givenIAmAtGraduationPage();

        graduationSteps
                .verifyCertificateIsThere();
    }

    private void checkFinance() throws InterruptedException {
        homeSteps
                .verifyThatIamAStudent()
                .givenIAmAtLibraryPage();

        librarySteps
                .givenIamBorrowABookAndVerify(borrowIsbn);

        Thread.sleep(8000);

        homeSteps
                .givenIAmAtFinancePage();
        Thread.sleep(1000);
        homeSteps
                .givenIAmAtLibraryPage();
        Thread.sleep(1000);
        homeSteps
                .givenIAmAtFinancePage();

        financeSteps
                .verifyFinances();
    }
}
