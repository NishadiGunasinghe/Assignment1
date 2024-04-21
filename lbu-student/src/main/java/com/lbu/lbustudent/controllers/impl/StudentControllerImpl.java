package com.lbu.lbustudent.controllers.impl;

import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.controllers.StudentController;
import com.lbu.lbustudent.dtos.StudentDto;
import com.lbu.lbustudent.dtos.StudentEnrolmentDto;
import com.lbu.lbustudent.models.Enrollment;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class StudentControllerImpl implements StudentController {

    private final StudentService studentService;
    private final ModelMapper modelMapper;

    public StudentControllerImpl(StudentService studentService, ModelMapper modelMapper) {
        this.studentService = studentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<StudentDto> createStudentEnrolment(
            StudentEnrolmentDto studentEnrolmentDto,
            String token) {
        Student student = studentService.createStudentEnrolment(
                studentEnrolmentDto.getAuthUserHref(),
                studentEnrolmentDto.getCourseHref(),
                token);
        StudentDto studentDto = convertStudentDto(student);
        studentDto.setJwtTokenDto(student.getJwtTokenDto());
        return ResponseEntity.ok(studentDto);
    }

    @Override
    public ResponseEntity<StudentDto> updateStudent(
            StudentDto studentDto,
            String authToken) {
        Student student;
        try {
            student = modelMapper.map(studentDto, Student.class);
        } catch (Exception e) {
            log.error("Model conversion error {}", studentDto);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Student updatedStudent = studentService.updateStudentDetails(student, authToken);
        return ResponseEntity.ok(convertStudentDto(updatedStudent));
    }

    @Override
    public ResponseEntity<StudentDto> getStudent(String authUserHref,
                                                 String studentId,
                                                 String authToken) {
        return ResponseEntity.ok(convertStudentDto(
                studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, studentId, authToken)));
    }

    private StudentDto convertStudentDto(Student student) {
        try {
            StudentDto studentDto = modelMapper.map(student, StudentDto.class);
            studentDto.setCourseHrefs(student.getEnrollments().stream().map(Enrollment::getCourseHref).collect(Collectors.toList()));
            return studentDto;
        } catch (Exception e) {
            log.error("Model conversion error {}", student);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
