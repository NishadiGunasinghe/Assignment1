package com.lbu.lbufunctionaltesting.page;


import com.lbu.lbufunctionaltesting.util.LogUtil;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.*;
import lombok.SneakyThrows;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BasePage {

    @Autowired
    protected WebDriver driver;
    @Autowired
    protected WebDriverWait wait;
    @Autowired
    protected JavascriptExecutor javascriptExecutor;
    @Autowired
    protected LogUtil logUtil;

    @PostConstruct
    private void init() {
        PageFactory.initElements(this.driver, this);
    }

    public abstract boolean isAt();

    public <T> void waitElement(T elementAttr) {
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            wait.until(ExpectedConditions.presenceOfElementLocated((By) elementAttr));
        } else {
            wait.until(ExpectedConditions.visibilityOf((WebElement) elementAttr));
        }
    }

    public <T> void waitElements(T elementAttr) {
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy((By) elementAttr));
        } else {
            wait.until(ExpectedConditions.visibilityOfAllElements((WebElement) elementAttr));
        }
    }

    //Click Method by using JAVA Generics (You can use both By or Web element)
    public <T> void pressKey(T elementAttr, Keys keys) {
        waitElement(elementAttr);
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            driver
                    .findElement((By) elementAttr)
                    .sendKeys(keys);
        } else {
            ((WebElement) elementAttr).sendKeys(keys);
        }
    }

    //Click Method by using JAVA Generics (You can use both By or Web element)
    public <T> void click(T elementAttr) {
        waitElement(elementAttr);
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            driver
                    .findElement((By) elementAttr)
                    .click();
        } else {
            ((WebElement) elementAttr).click();
        }
    }

    public void jsClick(By by) {
        javascriptExecutor.executeScript("arguments[0].click();", wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
    }

    //Write Text by using JAVA Generics (You can use both By or WebElement)
    public <T> void writeText(T elementAttr, String text) {
        waitElement(elementAttr);
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy((By) elementAttr));
            driver
                    .findElement((By) elementAttr)
                    .sendKeys(text);
        } else {
            wait.until(ExpectedConditions.visibilityOf((WebElement) elementAttr));
            ((WebElement) elementAttr).sendKeys(text);
        }
    }

    //Read Text by using JAVA Generics (You can use both By or WebElement)
    public <T> String readText(T elementAttr) {
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            return driver
                    .findElement((By) elementAttr)
                    .getText();
        } else {
            return ((WebElement) elementAttr).getText();
        }
    }

    //Read Text by using JAVA Generics (You can use both By or WebElement)
    public <T> WebElement findElement(T elementAttr) {
        try {
            if (elementAttr
                    .getClass()
                    .getName()
                    .contains("By")) {
                return driver
                        .findElement((By) elementAttr);
            } else {
                return ((WebElement) elementAttr);
            }
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @SneakyThrows
    public <T> String readTextErrorMessage(T elementAttr) {
        Thread.sleep(2000); //This needs to be improved.
        return driver
                .findElement((By) elementAttr)
                .getText();
    }

    //Close popup if exists
    public void handlePopup(By by) throws InterruptedException {
        waitElements(by);
        List<WebElement> popup = driver.findElements(by);
        if (!popup.isEmpty()) {
            popup
                    .get(0)
                    .click();
            Thread.sleep(200);
        }
    }

}
