package com.lbu.lbufunctionaltesting.page.course;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import com.lbu.lbufunctionaltesting.page.BasePage;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@LazyComponent
public class AllCoursePage extends BasePage {
    @Value("${wait.timeout}")
    Integer waitTimeout;
    @Value("${test-data.course.name}")
    String courseNamePrefix;

    @Value("${test-data.course.title}")
    String courseNameFullTitle;

    @FindBy(how = How.ID, using = "courseAutoCompleteSearch")
    WebElement courseAutoCompleteSearch;

    @FindBy(how = How.XPATH, using = "//*[@id=\"root\"]/div/main/div[2]/div/div[2]/div/div[1]/div/div/div[1]/div/span[1]")
    public WebElement courseTitle;

    @FindBy(how = How.XPATH, using = "//*[@id=\"root\"]/div/main/div[2]/div/div[2]/div/div[1]/div/div/div[3]/button")
    public WebElement courseViewMoreBtn;

    @FindBy(how = How.ID, using = "enrollBtn")
    public WebElement enrollButton;

    @FindBy(how = How.ID, using = "enrollMessage")
    public WebElement enrollMessage;


    By enrollCancelBtn = By.id("enrollCancelBtn");


    public AllCoursePage searchCourseTitle() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.courseAutoCompleteSearch.isDisplayed());
        writeText(this.courseAutoCompleteSearch, courseNamePrefix);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.courseAutoCompleteSearch.isDisplayed());
        pressKey(this.courseAutoCompleteSearch, Keys.ARROW_DOWN);
        pressKey(this.courseAutoCompleteSearch, Keys.ENTER);
        return this;
    }

    public AllCoursePage verifyCourseTitle() {
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.courseTitle.isDisplayed());
        assertEquals(courseNameFullTitle, readText(courseTitle));
        return this;
    }

    @SneakyThrows
    public AllCoursePage enrollCourse() {
        click(courseViewMoreBtn);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.enrollButton.isDisplayed());
        click(enrollButton);
        this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.enrollMessage.isDisplayed());
        assertEquals("Successfully enrolled for the subject : " + courseNameFullTitle, readText(enrollMessage));
        return this;
    }

    public AllCoursePage closeSuccessEnroll() {
        jsClick(enrollCancelBtn);
        return this;
    }

    @Override
    public boolean isAt() {
        return this.wait.withTimeout(Duration.ofSeconds(waitTimeout)).until((d) -> this.courseAutoCompleteSearch.isDisplayed());
    }
}
