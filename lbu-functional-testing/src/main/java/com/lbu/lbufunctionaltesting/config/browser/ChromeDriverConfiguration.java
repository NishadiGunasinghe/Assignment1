package com.lbu.lbufunctionaltesting.config.browser;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Slf4j
@Configuration
public class ChromeDriverConfiguration {

    private final ResourceLoader resourceLoader;

    public ChromeDriverConfiguration(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // This method sets the system property for the WebDriver executable path
    @PostConstruct
    private void setWebDriverExecutablePath() {
        try {
            // Load the WebDriver executable resource
            Resource resource = resourceLoader.getResource("classpath:chromedriver.exe");
            // Get the absolute path of the resource
            String absolutePath = resource.getFile().getAbsolutePath();
            // Set the system property for the WebDriver executable path
            System.setProperty("webdriver.chrome.driver", absolutePath);
        } catch (Exception e) {
            log.error("An error occurred while setting the driver path", e);
        }
    }
}
