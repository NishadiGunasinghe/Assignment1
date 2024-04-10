package com.lbu.lbustudent.repositories;

import com.lbu.lbustudent.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByAuthUserHref(String authUserHref);
}
