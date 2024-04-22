package com.lbu.lbufunctionaltesting.tests.signup;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.login.steps.LoginSteps;
import com.lbu.lbufunctionaltesting.page.signin.steps.SignInSteps;
import com.lbu.lbufunctionaltesting.tests.BaseTest;
import com.lbu.lbufunctionaltesting.tests.wrappers.CredentialWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class SignUpTest extends BaseTest {

    @LazyAutowired
    LoginSteps loginSteps;

    @LazyAutowired
    SignInSteps signInSteps;

    @Test
    public void WhenNewUser_ThenCreateAndActivate_ReturnActivatedAccount() {
        loginSteps
                .givenIAmAtLoginPage()
                .givenIAmCreatingANewAccount();
        CredentialWrapper credentialWrapper = signInSteps.getCredentialWrapper();
        signInSteps
                .whenIAmCreatingNewAccount(credentialWrapper);
    }


    @Test
    public void WhenExistingUser_ThenCreateAndActivate_ReturnError() {
        loginSteps
                .givenIAmAtLoginPage()
                .givenIAmCreatingANewAccount();
        CredentialWrapper credentialWrapper = signInSteps.getCredentialWrapper();
        signInSteps
                .whenIAmCreatingNewAccount(credentialWrapper);

        loginSteps
                .givenIAmAtLoginPage()
                .givenIAmCreatingANewAccount();

        signInSteps
                .whenIAmCreatingNewAccountWithSameDetails(credentialWrapper);
    }

}
