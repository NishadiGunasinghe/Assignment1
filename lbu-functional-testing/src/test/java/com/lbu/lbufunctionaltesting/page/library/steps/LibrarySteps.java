package com.lbu.lbufunctionaltesting.page.library.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.library.LibraryPage;

@LazyComponent
public class LibrarySteps {

    @LazyAutowired
    LibraryPage libraryPage;

    public LibrarySteps givenIamBorrowABookAndVerify(String borrowIsbn) {
        libraryPage
                .borrowBookAndVerify(borrowIsbn);
        return this;
    }

    public LibrarySteps givenIamReturnABookAndVerify(String returnIsbn) {
        libraryPage
                .returnBookAndVerify(returnIsbn);
        return this;
    }
}
