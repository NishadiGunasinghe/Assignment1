package com.lbu.lbufunctionaltesting.page.library;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.BasePage;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@LazyComponent
public class LibraryPage extends BasePage {
    @Value("${wait.timeout}")
    Integer waitTimeout;
    @Value("${test-data.library.borrow.title}")
    String borrowTitle;

    @Value("${test-data.library.return.title}")
    String returnTitle;

    @Value("${test-data.library.borrow.isbn}")
    String borrowIsbn;

    @Value("${test-data.library.return.isbn}")
    String returnIsbn;

    @FindBy(how = How.XPATH, using = "//*[@id=\"root\"]/div/main/div[2]/div[1]/div/div/h4")
    public WebElement titleBookPage;

    By returnBtnDig = By.id("returnBtn");
    By borrowBtnDig = By.id("borrowBtn");

    By messageElement = By.id("returnMessage");
    By cancelBtn = By.id("returnCancelBtn");

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(1)).until((d) -> this.titleBookPage.isDisplayed());
    }

    @SneakyThrows
    public LibraryPage borrowBookAndVerify(String isbn) {
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.titleBookPage.isDisplayed());
        By borrowBtn = By.id("borrowBtn" + isbn);
        jsClick(borrowBtn);
        WebElement borrow = findElement(borrowBtnDig);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> borrow.isDisplayed());
        jsClick(borrowBtnDig);
        Thread.sleep(1000);
        WebElement borrowMsg = findElement(messageElement);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> borrowMsg.isDisplayed());
        if (borrowIsbn.equals(isbn)) {
            assertEquals("Successfully borrowed the book " + borrowTitle, readText(borrowMsg));
        } else if (returnIsbn.equals(isbn)) {
            assertEquals("Successfully borrowed the book " + returnTitle, readText(borrowMsg));
        } else {
            fail();
        }
        jsClick(cancelBtn);
        return this;
    }

    @SneakyThrows
    public LibraryPage returnBookAndVerify(String isbn) {
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.titleBookPage.isDisplayed());
        By returnBtnBtn = By.id("returnBtn" + isbn);
        jsClick(returnBtnBtn);
        WebElement returnButton = findElement(returnBtnDig);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> returnButton.isDisplayed());
        jsClick(returnBtnDig);
        Thread.sleep(1000);
        WebElement returnMsg = findElement(messageElement);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> returnMsg.isDisplayed());
        if (borrowIsbn.equals(isbn)) {
            assertEquals("Successfully returned the book " + borrowTitle, readText(returnMsg));
        } else if (returnIsbn.equals(isbn)) {
            assertEquals("Successfully returned the book " + returnTitle, readText(returnMsg));
        } else {
            fail();
        }
        jsClick(cancelBtn);
        return this;
    }
}
