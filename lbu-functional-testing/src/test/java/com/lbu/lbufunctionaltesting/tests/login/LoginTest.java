package com.lbu.lbufunctionaltesting.tests.login;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.home.steps.HomeSteps;
import com.lbu.lbufunctionaltesting.page.login.steps.LoginSteps;
import com.lbu.lbufunctionaltesting.page.signin.steps.SignInSteps;
import com.lbu.lbufunctionaltesting.services.auth.database.AccountActivationDetails;
import com.lbu.lbufunctionaltesting.services.auth.database.AccountActivationDetailsRepository;
import com.lbu.lbufunctionaltesting.services.auth.services.AuthService;
import com.lbu.lbufunctionaltesting.tests.BaseTest;
import com.lbu.lbufunctionaltesting.tests.wrappers.CredentialWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Execution(ExecutionMode.CONCURRENT)
public class LoginTest extends BaseTest {

    @LazyAutowired
    LoginSteps loginSteps;

    @LazyAutowired
    HomeSteps homeSteps;

    @LazyAutowired
    SignInSteps signInSteps;

    CredentialWrapper credentialWrapper;

    @Autowired
    AuthService authService;

    @Autowired
    AccountActivationDetailsRepository accountActivationDetailsRepository;

    @BeforeEach
    public void setup() {
        loginSteps
                .givenIAmAtLoginPage()
                .givenIAmCreatingANewAccount();
        credentialWrapper = signInSteps.getCredentialWrapper();
        signInSteps
                .whenIAmCreatingNewAccount(credentialWrapper);
    }

    @Test
    public void WhenInvalidUserNamePassword_ThenTryLogin_ReturnLoginError() {
        loginSteps
                .givenIAmAtLoginPage()
                .whenILogin("testing", "testing")
                .thenIVerifyInvalidLoginMessage("The username or password provided is invalid. Please try again with the correct username and password.");
    }

    @Test
    public void WhenInactiveUserNamePassword_ThenTryLogin_ReturnError() {
        loginSteps
                .givenIAmAtLoginPage()
                .whenILogin(credentialWrapper.getUserName(), credentialWrapper.getPassword())
                .thenIVerifyInvalidLoginMessage("The account is not activated. Please activate it before logging in.");
    }

    @Test
    public void WhenValidUserNamePassword_ThenTryLogin_ReturnWelcomeMessage() throws InterruptedException {
        Optional<AccountActivationDetails> byUserUsername = accountActivationDetailsRepository.findByUser_Username(credentialWrapper.getUserName());
        if (byUserUsername.isPresent()) {
            AccountActivationDetails accountActivationDetails = byUserUsername.get();
            authService.activateUser(accountActivationDetails.getToken());
            Thread.sleep(1000);
            loginSteps
                    .givenIAmAtLoginPage()
                    .whenILogin(credentialWrapper.getUserName(), credentialWrapper.getPassword());
            homeSteps
                    .verifyHomeWelcomeMessage();
        } else {
            Assertions.fail();
        }
    }

}
