package com.lbu.lbulibrary.repositories;

import com.lbu.lbulibrary.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findStudentByAuthUserHref(String authUserHref);
}
