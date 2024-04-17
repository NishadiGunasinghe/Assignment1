package com.lbu.lbustudent.controllers.impl;

import com.lbu.lbustudent.commons.exeptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.auth.services.AuthService;
import com.lbu.lbustudent.controllers.StudentController;
import com.lbu.lbustudent.dtos.StudentDto;
import com.lbu.lbustudent.dtos.StudentEnrolmentDto;
import com.lbu.lbustudent.models.Enrolment;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.services.StudentService;
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
    private final AuthService authService;

    public StudentControllerImpl(StudentService studentService, ModelMapper modelMapper, AuthService authService) {
        this.studentService = studentService;
        this.modelMapper = modelMapper;
        this.authService = authService;
    }


    /**
     * Creates a student enrolment based on the provided student enrolment DTO and authentication token.
     * Validates the course and authentication user hrefs.
     *
     * @param studentEnrolmentDto The DTO containing student enrolment information.
     * @param token                The authentication token.
     * @return ResponseEntity containing the created or updated student DTO.
     */
    @Override
    public ResponseEntity<StudentDto> createStudentEnrolment(
            StudentEnrolmentDto studentEnrolmentDto,
            String token) {
        validateHrefAndGetId(studentEnrolmentDto.getCourseHref());
        validateHrefAndGetId(studentEnrolmentDto.getAuthUserHref());
        authService.validateAuthUserHref(studentEnrolmentDto.getAuthUserHref(), token);
        Student student = studentService.createStudentEnrolment(
                studentEnrolmentDto.getAuthUserHref(),
                studentEnrolmentDto.getCourseHref(),
                token);
        StudentDto studentDto = convertStudentDto(student);
        studentDto.setJwtTokenDto(student.getJwtTokenDto());
        return ResponseEntity.ok(studentDto);
    }

    /**
     * Updates student details based on the provided student DTO and authentication token.
     * Validates the student DTO and authentication user href.
     *
     * @param studentDto The DTO containing student information.
     * @param authToken  The authentication token.
     * @return ResponseEntity containing the updated student DTO.
     */
    @Override
    public ResponseEntity<StudentDto> updateStudent(
            StudentDto studentDto,
            String authToken) {
        validateStudentDto(studentDto);
        authService.validateAuthUserHref(studentDto.getAuthUserHref(), authToken);
        Student student;
        try {
            student = modelMapper.map(studentDto, Student.class);
        } catch (Exception e) {
            log.error("Model conversion error {}", studentDto);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Student updatedStudent = studentService.updateStudentDetails(student);
        return ResponseEntity.ok(convertStudentDto(updatedStudent));
    }

    /**
     * Retrieves student details based on the provided authentication user href, student ID, and authentication token.
     * Validates the authentication user href.
     *
     * @param authUserHref The authentication user href.
     * @param studentId    The student ID.
     * @param authToken    The authentication token.
     * @return ResponseEntity containing the student DTO.
     */
    @Override
    public ResponseEntity<StudentDto> getStudent(String authUserHref,
                                                 String studentId,
                                                 String authToken) {
        authService.validateAuthUserHref(authUserHref, authToken);
        Student student = studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, studentId);
        return ResponseEntity.ok(convertStudentDto(student));
    }

    /**
     * Converts a Student entity to a StudentDto object.
     *
     * @param student The Student entity to convert.
     * @return The converted StudentDto object.
     */
    private StudentDto convertStudentDto(Student student) {
        try {
            StudentDto studentDto = modelMapper.map(student, StudentDto.class);
            studentDto.setCourseHrefs(student.getEnrollments().stream().map(Enrolment::getCourseHref).collect(Collectors.toList()));
            return studentDto;
        } catch (Exception e) {
            log.error("Model conversion error {}", student);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}