package com.lbu.lbufunctionaltesting.util;

import com.lbu.lbufunctionaltesting.annotations.LazyComponent;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;

@LazyComponent
public class LogUtil {

    public static LogEntries getLogs(WebDriver driver) {
        return driver
                .manage()
                .logs()
                .get(LogType.BROWSER);
    }

    public void isLoginErrorLog(WebDriver driver) {
        //Check logs (works only Chrome and Edge)
        LogEntries logEntries = driver
                .manage()
                .logs()
                .get(LogType.BROWSER);
        Assert.assertTrue(logEntries
                .getAll()
                .stream()
                .anyMatch(logEntry -> logEntry
                        .getMessage()
                        .contains("An invalid email address was specified")));
    }

}
