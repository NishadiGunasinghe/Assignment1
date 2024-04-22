package com.lbu.lbufunctionaltesting.annotations;

import com.lbu.lbufunctionaltesting.LbuFunctionalTestingApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LbuFunctionalTestingApplication.class)
public @interface SeleniumTest {
}
