package com.lbu.lbufunctionaltesting.page.home;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.BasePage;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@LazyComponent
public class HomePage extends BasePage {
    @Value("${wait.timeout}")
    Integer waitTimeout;
    @FindBy(how = How.ID, using = "homeWelcome")
    public WebElement welcomeMessage;

    /*sidebar buttons user & student*/

    @FindBy(how = How.ID, using = "btnHome")
    public WebElement btnHome;
    @FindBy(how = How.ID, using = "btnCourse")
    public WebElement btnCourse;
    @FindBy(how = How.ID, using = "btnProfile")
    public WebElement btnProfile;
    @FindBy(how = How.ID, using = "btnLogout")
    public WebElement btnLogout;

    /*sidebar buttons student & admin*/
    By btnGraduation = By.id("btnGraduation");
    By btnFinance = By.id("btnFinance");
    By btnLibrary = By.id("btnLibrary");

    @FindBy(how = How.ID, using = "btnAll Courses")
    public WebElement btnAllCourse;

    @FindBy(how = How.ID, using = "btnView Books")
    public WebElement btnViewBooks;


    public HomePage goToAllCoursePage() {
        click(btnCourse);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.btnAllCourse.isDisplayed());
        click(btnAllCourse);
        return this;
    }


    public HomePage verifyWelcomeMessage() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.welcomeMessage.isDisplayed());
        assertEquals("Welcome to Student Hive", readText(welcomeMessage));
        return this;
    }

    @SneakyThrows
    public HomePage verifyStudentActions() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.btnHome.isDisplayed());
        assertTrue(btnHome.isDisplayed(), "Home is available");
        assertTrue(btnCourse.isDisplayed(), "Course is available");
        assertTrue(btnProfile.isDisplayed(), "Profile is available");
        assertTrue(btnLogout.isDisplayed(), "Logout is available");
        assertTrue(findElement(btnGraduation).isDisplayed(), "Graduation is available");
        assertTrue(findElement(btnFinance).isDisplayed(), "Finance is available");
        assertTrue(findElement(btnLibrary).isDisplayed(), "Library is available");
        return this;
    }

    @SneakyThrows
    public HomePage verifyUserActions() {
        click(this.btnHome);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.welcomeMessage.isDisplayed());
        assertTrue(btnHome.isDisplayed(), "Home is available");
        assertTrue(btnCourse.isDisplayed(), "Course is available");
        assertTrue(btnProfile.isDisplayed(), "Profile is available");
        assertTrue(btnLogout.isDisplayed(), "Logout is available");

        assertNull(findElement(btnGraduation), "Graduation is available");
        assertNull(findElement(btnFinance), "Finance is available");
        assertNull(findElement(btnLibrary), "Library is available");
        return this;
    }

    public HomePage logout() {
        click(btnLogout);
        return this;
    }

    public HomePage goToViewBooksPage() {
        jsClick(btnLibrary);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.btnViewBooks.isDisplayed());
        click(btnViewBooks);
        return this;
    }

    public HomePage goToFinancePage() {
        jsClick(btnFinance);
        return this;
    }

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.welcomeMessage.isDisplayed());
    }

    public HomePage goToGraduationPage() {
        jsClick(btnGraduation);
        return this;
    }
}
