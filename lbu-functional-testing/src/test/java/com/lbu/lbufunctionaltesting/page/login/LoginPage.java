package com.lbu.lbufunctionaltesting.page.login;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@LazyComponent
public class LoginPage extends BasePage {
    @Value("${wait.timeout}")
    Integer waitTimeout;
    @Value("${application.url}")
    private String baseURL;

    //********* Web Elements by using Page Factory *********
    @FindBy(how = How.ID, using = "email")
    public WebElement userName;
    @FindBy(how = How.ID, using = "password")
    public WebElement password;

    @FindBy(how = How.ID, using = "btnSignIn")
    public WebElement btnSignIn;

    @FindBy(how = How.ID, using = "loginErrorMessage")
    public WebElement loginErrorMessage;

    @FindBy(how = How.ID, using = "signUpLink")
    public WebElement signUpLink;

    //Go to LoginPage
    public LoginPage goToLoginPage() {
        driver.get(baseURL);
        return this;
    }

    public LoginPage goToHomePage(){
        return this;
    }
    public LoginPage goToSignUpPage(){
        click(signUpLink);
        return this;
    }

    //*********Page Methods*********
    public LoginPage login(String userName, String password) {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.userName.isDisplayed());
        writeText(this.userName, userName);
        writeText(this.password, password);
        click(btnSignIn);
        return this;
    }

    public LoginPage verifyLoginErrorMessage(String expectedText) {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.loginErrorMessage.isDisplayed());
        assertEquals(expectedText, readText(loginErrorMessage));
        return this;
    }

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.userName.isDisplayed());
    }
}
