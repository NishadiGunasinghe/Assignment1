package com.lbu.lbufunctionaltesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LbuFunctionalTestingApplication {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        SpringApplication.run(LbuFunctionalTestingApplication.class, args);
    }

}
