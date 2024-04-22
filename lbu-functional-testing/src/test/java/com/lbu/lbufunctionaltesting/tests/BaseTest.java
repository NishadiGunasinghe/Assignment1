package com.lbu.lbufunctionaltesting.tests;

import com.lbu.lbufunctionaltesting.annotations.LazyAutowired;
import com.lbu.lbufunctionaltesting.annotations.SeleniumTest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;

@Slf4j
@SeleniumTest
@Getter
public class BaseTest {

    @BeforeEach
    public void setup() {
    }
    @LazyAutowired
    public ApplicationContext applicationContext;
    @AfterEach
    public void teardown() {
        this.applicationContext
                .getBean(WebDriver.class)
                .quit();
    }

}
