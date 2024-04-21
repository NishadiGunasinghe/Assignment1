package com.lbu.lbulibrary.service;


import com.lbu.lbulibrary.models.Student;

public interface StudentService {
    void createNewStudent(Student student);

    Student getStudentByAuthUserHref(String authUserHref);

    void borrowBook(String isbn, String authUserHref);

    void returnBook(String isbn, String authUserHref);
}
