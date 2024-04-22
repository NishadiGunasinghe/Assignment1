package com.lbu.lbufunctionaltesting.config;

import com.lbu.lbufunctionaltesting.annotations.LazyConfiguration;
import com.lbu.lbufunctionaltesting.annotations.WebdriverScopeBean;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@LazyConfiguration
public class WebDriverConfig {

    @WebdriverScopeBean
    @ConditionalOnProperty(name = "browser", havingValue = "firefox")
    @Primary
    public WebDriver firefoxDriver() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setNoProxy("no_proxy-var");
        firefoxOptions.setCapability("proxy", proxy);
        return new FirefoxDriver(firefoxOptions);
    }

    @WebdriverScopeBean
    @ConditionalOnProperty(name = "browser", havingValue = "edge")
    @Primary
    public WebDriver edgeDriver() {
        return new EdgeDriver();
    }

    @WebdriverScopeBean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "browser", havingValue = "chrome")
    @Primary
    public WebDriver chromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofHours(1));
        return chromeDriver;
    }

}
