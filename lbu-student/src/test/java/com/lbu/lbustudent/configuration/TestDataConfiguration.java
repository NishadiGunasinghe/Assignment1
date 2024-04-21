package com.lbu.lbustudent.configuration;

import com.lbu.lbustudent.models.Enrollment;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.repositories.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
public class TestDataConfiguration {

    @Autowired
    private StudentRepository studentRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i < 9; i++) {
            Student student1 = new Student();
            student1.setAddress("Test address" + i);
            student1.setDateOfBirth("1991-06-2" + i);
            student1.setEmergencyContact("0758885918" + i);
            student1.setAuthUserHref("/auth/user/" + UUID.randomUUID());
            Enrollment enrollment1 = new Enrollment();
            enrollment1.setStudent(student1);
            enrollment1.setCourseHref("/courses/" + UUID.randomUUID());
            student1.setEnrollments(List.of(enrollment1));
            studentRepository.save(student1);
        }
    }

}
