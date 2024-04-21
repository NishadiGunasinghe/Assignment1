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

    /**
     * Creates a new student enrolment based on the provided student enrolment data and authorization token.
     * It delegates the creation of the student enrolment to the studentService, then converts the resulting
     * Student object to a StudentDto, attaches the JWT token DTO, and returns the ResponseEntity with the studentDto.
     *
     * @param studentEnrolmentDto The data for creating the student enrolment.
     * @param token               The authorization token for the request.
     * @return ResponseEntity containing the created StudentDto.
     */
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

    /**
     * Updates the details of a student based on the provided studentDto and authorization token.
     * It maps the studentDto to a Student object, catches any mapping exceptions, then delegates the update
     * operation to the studentService. Finally, it converts the updated Student to a StudentDto and returns
     * the ResponseEntity containing the updated StudentDto.
     *
     * @param studentDto The data for updating the student.
     * @param authToken  The authorization token for the request.
     * @return ResponseEntity containing the updated StudentDto.
     */
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

    /**
     * Retrieves the details of a student based on the provided authUserHref, studentId, and authorization token.
     * It delegates the retrieval operation to the studentService, then converts the retrieved Student to a StudentDto
     * and returns the ResponseEntity containing the StudentDto.
     *
     * @param authUserHref The href of the authenticated user.
     * @param studentId    The ID of the student.
     * @param authToken    The authorization token for the request.
     * @return ResponseEntity containing the StudentDto.
     */
    @Override
    public ResponseEntity<StudentDto> getStudent(String authUserHref,
                                                 String studentId,
                                                 String authToken) {
        return ResponseEntity.ok(convertStudentDto(
                studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, studentId, authToken)));
    }

    /**
     * Converts a Student object to a StudentDto.
     * It uses ModelMapper for the conversion and sets additional attributes such as courseHrefs.
     * Any exceptions occurring during the conversion are caught and rethrown as an LBUStudentsRuntimeException.
     *
     * @param student The student object to convert.
     * @return The converted StudentDto.
     */
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
