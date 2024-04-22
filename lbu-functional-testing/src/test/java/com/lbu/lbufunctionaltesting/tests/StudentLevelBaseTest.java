package com.lbu.lbufunctionaltesting.tests;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.page.course.steps.CourseSteps;
import com.lbu.lbufunctionaltesting.page.home.steps.HomeSteps;
import com.lbu.lbufunctionaltesting.page.login.steps.LoginSteps;
import com.lbu.lbufunctionaltesting.page.signin.steps.SignInSteps;
import com.lbu.lbufunctionaltesting.services.auth.database.AccountActivationDetails;
import com.lbu.lbufunctionaltesting.services.auth.database.AccountActivationDetailsRepository;
import com.lbu.lbufunctionaltesting.services.auth.services.AuthService;
import com.lbu.lbufunctionaltesting.tests.wrappers.CredentialWrapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Execution(ExecutionMode.CONCURRENT)
public class StudentLevelBaseTest extends BaseTest {

    @LazyAutowired
    LoginSteps loginSteps;

    @LazyAutowired
    protected HomeSteps homeSteps;

    @LazyAutowired
    SignInSteps signInSteps;

    CredentialWrapper credentialWrapper;

    @Autowired
    AuthService authService;

    @Autowired
    AccountActivationDetailsRepository accountActivationDetailsRepository;

    @LazyAutowired
    CourseSteps courseSteps;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        loginSteps
                .givenIAmAtLoginPage()
                .givenIAmCreatingANewAccount();
        credentialWrapper = signInSteps.getCredentialWrapper();
        signInSteps
                .whenIAmCreatingNewAccount(credentialWrapper);

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

            homeSteps
                    .givenIAmAtAllCoursePage();
            courseSteps
                    .givenIamTryingToSearchCourse()
                    .givenIamTryingToEnrollCourse()
                    .givenIamTryingToCancel();
        } else {
            Assertions.fail();
        }
    }

}
