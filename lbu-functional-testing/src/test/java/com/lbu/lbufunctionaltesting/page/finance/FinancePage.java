package com.lbu.lbufunctionaltesting.page.finance;

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
public class FinancePage extends BasePage {


    @Value("${wait.timeout}")
    Integer waitTimeout;
    @FindBy(how = How.XPATH, using = "//*[@id=\"root\"]/div/main/div[2]/div/div[1]/div/h4")
    public WebElement financeTitle;

    By fineType0 = By.id("celltype0");
    By fineType1 = By.id("celltype1");

    By financeStatus0 = By.id("cellstatus0");
    By financeStatus1 = By.id("cellstatus1");

    By paymentBtn0 = By.id("paymentBtn0");
    By cancelBtn1 = By.id("cancelBtn1");

    By payConfirmationMessage = By.id("payConfirmationMessage");
    By payCancellationMessage = By.id("payCancellationMessage");
    By payCancellationOkBtn = By.id("payCancellationOkBtn");

    By payConfirmationOkBtn = By.id("payConfirmationOkBtn");

    By financeSuccessCancelBtn = By.id("financeSuccessCancelBtn");
    By financeSuccessMessage = By.id("financeSuccessMessage");

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.financeTitle.isDisplayed());
    }

    @SneakyThrows
    public FinancePage verifyFinances() {
        Thread.sleep(5000);
        String type0 = readText(fineType0);
        verifyType(type0);
        Thread.sleep(5000);
        String type1 = readText(fineType1);
        verifyType(type1);
        return this;
    }

    private void verifyType(String type) {
        if (type.equals("Library Fine")) {
            assertEquals("Library Fine", type);
        } else if (type.equals("Tuition Fee")) {
            assertEquals("Tuition Fee", type);
        } else {
            fail();
        }
    }

    public FinancePage verifyFinancesAreInPending() {
        assertEquals("Outstanding", readText(financeStatus0));
        assertEquals("Outstanding", readText(financeStatus1));
        return this;
    }

    @SneakyThrows
    public FinancePage initPaymentFirstInvoice() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.financeTitle.isDisplayed());
        jsClick(paymentBtn0);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(payConfirmationMessage).getText());
        assertEquals("Do you wanted to pay ?", readText(payConfirmationMessage));
        jsClick(payConfirmationOkBtn);
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(financeSuccessMessage).getText());
        assertEquals("Given invoice successfully payed.", readText(financeSuccessMessage));
        jsClick(financeSuccessCancelBtn);
        return this;
    }

    @SneakyThrows
    public FinancePage initCancelSecondInvoice() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.financeTitle.isDisplayed());
        jsClick(cancelBtn1);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(payCancellationMessage).getText());
        assertEquals("Do you wanted to cancel the pay ?", readText(payCancellationMessage));
        jsClick(payCancellationOkBtn);
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(financeSuccessMessage).getText());
        assertEquals("Given invoice successfully cancelled.", readText(financeSuccessMessage));
        jsClick(financeSuccessCancelBtn);
        return this;
    }
}
