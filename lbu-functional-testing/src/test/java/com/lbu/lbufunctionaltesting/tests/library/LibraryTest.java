package com.lbu.lbufunctionaltesting.tests.library;


import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.home.steps.HomeSteps;
import com.lbu.lbufunctionaltesting.page.library.steps.LibrarySteps;
import com.lbu.lbufunctionaltesting.tests.StudentLevelBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class LibraryTest extends StudentLevelBaseTest {

    @Value("${test-data.library.borrow.isbn}")
    String borrowIsbn;

    @Value("${test-data.library.return.isbn}")
    String returnIsbn;

    @LazyAutowired
    HomeSteps homeSteps;

    @LazyAutowired
    LibrarySteps librarySteps;

    @Test
    public void WhenLoggedIn_ThenVerifyStudentAccess_ReturnStudentUI() {
        homeSteps
                .verifyThatIamAStudent()
                .givenIAmAtLibraryPage();
    }

    @Test
    public void WhenNeedBooks_ThenGoToLibraryToBorrowBook_ReturnSuccessBorrow() {
        homeSteps
                .verifyThatIamAStudent()
                .givenIAmAtLibraryPage();
        librarySteps
                .givenIamBorrowABookAndVerify(borrowIsbn);

    }

    @Test
    public void WhenBookReturns_ThenGoToLibraryAndReturn_ReturnSuccessfully() throws InterruptedException {
        homeSteps
                .verifyThatIamAStudent()
                .givenIAmAtLibraryPage();
        librarySteps
                .givenIamBorrowABookAndVerify(borrowIsbn);
        Thread.sleep(1000);
        librarySteps
                .givenIamBorrowABookAndVerify(returnIsbn)
                .givenIamReturnABookAndVerify(returnIsbn);
    }

}
