package com.lbu.lbufunctionaltesting.page.graduation;

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

@LazyComponent
public class GraduationPage extends BasePage {

    @Value("${wait.timeout}")
    Integer waitTimeout;

    @FindBy(how = How.ID, using = "graduationTitle")
    public WebElement graduationTitle;

    By certificationTitle = By.id("certificationTitle");

    By graduationLetterTitle = By.id("graduationLetterTitle");


    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.graduationTitle.isDisplayed());
    }

    @SneakyThrows
    public GraduationPage verifyCertificate() {
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(certificationTitle).isDisplayed());
        assertEquals("CERTIFICATE", readText(certificationTitle));
        return this;
    }

    @SneakyThrows
    public GraduationPage verifyLetter() {
        Thread.sleep(1000);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> findElement(graduationLetterTitle).isDisplayed());
        assertEquals("Graduation Status Letter", readText(graduationLetterTitle));
        return this;
    }
}
