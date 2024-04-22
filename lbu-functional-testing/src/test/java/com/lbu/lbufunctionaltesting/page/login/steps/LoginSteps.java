package com.lbu.lbufunctionaltesting.page.login.steps;

import com.lbu.lbufunctionaltesting.annotations.ElapsedTime;
import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.login.LoginPage;

@LazyComponent
public class LoginSteps {

    @LazyAutowired
    LoginPage loginPage;

    public LoginSteps givenIAmAtLoginPage() {
        loginPage.goToLoginPage();
        return this;
    }

    @ElapsedTime
    public LoginSteps givenIAmCreatingANewAccount() {
        loginPage.goToSignUpPage();
        return this;
    }

    @ElapsedTime
    public LoginSteps whenILogin(String userName, String password) {
        loginPage.login(userName, password);
        return this;
    }

    @ElapsedTime
    public LoginSteps thenIVerifyInvalidLoginMessage(String errorMessage) {
        loginPage.verifyLoginErrorMessage(errorMessage);
        return this;
    }

}
