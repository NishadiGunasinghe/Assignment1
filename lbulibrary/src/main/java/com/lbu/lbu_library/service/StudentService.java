package com.lbu.lbu_library.service;


import com.lbu.lbu_library.models.Student;

public interface StudentService {
    void createNewStudent(Student student);

    Student getStudentByAuthUserHref(String authUserHref);

    void borrowBook(String isbn, String authUserHref);

    void returnBook(String isbn, String authUserHref);
}
