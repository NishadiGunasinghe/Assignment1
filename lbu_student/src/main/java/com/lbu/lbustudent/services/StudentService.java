package com.lbu.lbustudent.services;

import com.lbu.lbustudent.models.Student;

public interface StudentService {

    Student createStudentEnrolment(String authUserHref, String courseHref, String token);

    Student updateStudentDetails(Student student);

    Student getStudentDetailsFromAuthIdOrStudentId(String id, String studentId);
}
