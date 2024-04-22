package com.lbu.lbufunctionaltesting.page.signin.steps;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.signin.SignInPage;
import com.lbu.lbufunctionaltesting.tests.wrappers.CredentialWrapper;
import org.springframework.beans.factory.annotation.Value;

@LazyComponent
public class SignInSteps {

    @LazyAutowired
    SignInPage signInPage;

    @Value("${custom.credential.prefix}")
    private String credentialPrefix;

    public CredentialWrapper getCredentialWrapper(){
        long currentTimeMillis = System.currentTimeMillis();
        String userName = credentialPrefix + currentTimeMillis;
        String password = credentialPrefix + currentTimeMillis;
        String firstName = "Testing FirstName " + currentTimeMillis;
        String lastName = "Testing LastName " + currentTimeMillis;
        String email = credentialPrefix + currentTimeMillis + "@gmail.com";
        return new CredentialWrapper(firstName, lastName, userName, password, email);
    }

    public SignInSteps whenIAmCreatingNewAccount(CredentialWrapper credentialWrapper) {
        signInPage.createAccount(credentialWrapper, true);
        return this;
    }

    public SignInSteps whenIAmCreatingNewAccountWithSameDetails(CredentialWrapper credentialWrapper) {
        signInPage.createAccount(credentialWrapper, false);
        return this;
    }


}
