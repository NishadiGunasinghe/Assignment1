package com.lbu.lbustudent.unittests;

import com.lbu.lbustudent.LbuStudentApplication;
import com.lbu.lbustudent.configuration.TestDataConfiguration;
import com.lbu.lbustudent.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {LbuStudentApplication.class, TestDataConfiguration.class})
public abstract class StudentTestConfig {

    @Autowired
    protected StudentRepository studentRepository;
}
