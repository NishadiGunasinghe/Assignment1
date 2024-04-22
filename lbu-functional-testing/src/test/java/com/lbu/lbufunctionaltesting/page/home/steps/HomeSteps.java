package com.lbu.lbufunctionaltesting.page.home.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.home.HomePage;

@LazyComponent
public class HomeSteps {

    @LazyAutowired
    HomePage homePage;

    public HomeSteps verifyHomeWelcomeMessage() {
        homePage
                .verifyWelcomeMessage();
        return this;
    }

    public HomeSteps givenIAmAtAllCoursePage() {
        homePage
                .verifyWelcomeMessage()
                .goToAllCoursePage();
        return this;
    }

    public HomeSteps verifyThatIamAStudent() {
        homePage
                .verifyStudentActions();
        return this;
    }

    public HomeSteps verifyThatIamAUser() {
        homePage
                .verifyUserActions();
        return this;
    }

    public HomeSteps givenIamLogoutUser() {
        homePage.logout();
        return this;
    }

    public HomeSteps givenIAmAtLibraryPage() {
        homePage.goToViewBooksPage();
        return this;
    }

    public HomeSteps givenIAmAtFinancePage() {
        homePage.goToFinancePage();
        return this;
    }

    public HomeSteps givenIAmAtGraduationPage() {
        homePage.goToGraduationPage();
        return this;
    }
}
