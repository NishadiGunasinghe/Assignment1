package com.lbu.lbufunctionaltesting.page.signin;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.BasePage;
import com.lbu.lbufunctionaltesting.tests.wrappers.CredentialWrapper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@LazyComponent
public class SignInPage extends BasePage {
    @Value("${wait.timeout}")
    Integer waitTimeout;
    @FindBy(how = How.ID, using = "firstName")
    public WebElement firstName;

    @FindBy(how = How.ID, using = "lastName")
    public WebElement lastName;

    @FindBy(how = How.ID, using = "email")
    public WebElement email;

    @FindBy(how = How.ID, using = "userName")
    public WebElement userName;

    @FindBy(how = How.ID, using = "password")
    public WebElement password;

    @FindBy(how = How.ID, using = "btnSignIn")
    public WebElement btnSignIn;

    @FindBy(how = How.ID, using = "successMessage")
    public WebElement successMessage;

    @FindBy(how = How.ID, using = "signUpErrorMessage")
    public WebElement signUpErrorMessage;

    public SignInPage createAccount(CredentialWrapper credentialWrapper, boolean success) {
        writeText(this.firstName, credentialWrapper.getFirstName());
        writeText(this.lastName, credentialWrapper.getLastName());
        writeText(this.email, credentialWrapper.getEmail());
        writeText(this.password, credentialWrapper.getPassword());
        writeText(this.userName, credentialWrapper.getPassword());
        click(btnSignIn);
        if (success) {
            this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.successMessage.isDisplayed());
            assertEquals("Successfully created the account for email: " + credentialWrapper.getEmail() + " and user: " + credentialWrapper.getUserName(), readText(successMessage));
        } else {
            this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.signUpErrorMessage.isDisplayed());
            assertEquals("The provided username or email already available.", readText(signUpErrorMessage));
        }
        return this;
    }

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.userName.isDisplayed());
    }
}
