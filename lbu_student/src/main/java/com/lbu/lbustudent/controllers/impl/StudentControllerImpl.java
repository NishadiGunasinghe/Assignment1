package com.lbu.lbustudent.controllers.impl;

import com.lbu.lbustudent.commons.exeptions.LBUStudentsRuntimeException;
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

    public StudentControllerImpl(StudentService studentService, ModelMapper modelMapper) {
        this.studentService = studentService;
        this.modelMapper = modelMapper;
    }


    /**
     * Creates a new student enrolment based on the provided StudentEnrolmentDto and token.
     * Validates the course and authentication user hrefs before creating the enrolment.
     *
     * @param studentEnrolmentDto The DTO containing information about the student enrolment.
     * @param token               The token for authorization.
     * @return ResponseEntity<StudentDto> ResponseEntity containing the created student DTO.
     */
    @Override
    public ResponseEntity<StudentDto> createStudentEnrolment(
            StudentEnrolmentDto studentEnrolmentDto,
            String token) {
        // Validate course href and authentication user href
        validateHrefAndGetId(studentEnrolmentDto.getCourseHref());
        validateHrefAndGetId(studentEnrolmentDto.getAuthUserHref());

        // Create student enrolment and retrieve the corresponding student
        Student student = studentService.createStudentEnrolment(
                studentEnrolmentDto.getAuthUserHref(),
                studentEnrolmentDto.getCourseHref(),
                token);

        // Convert student to student DTO and return ResponseEntity with student DTO
        StudentDto studentDto = convertStudentDto(student);
        return ResponseEntity.ok(studentDto);
    }

    /**
     * Converts a Student object to a StudentDto object.
     * Sets the course hrefs for the student DTO based on the enrolments of the student.
     *
     * @param student The Student object to be converted to StudentDto.
     * @return StudentDto The converted StudentDto object.
     * @throws LBUStudentsRuntimeException if an internal error occurs during conversion.
     */
    private StudentDto convertStudentDto(Student student) {
        try {
            // Map student to student DTO
            StudentDto studentDto = modelMapper.map(student, StudentDto.class);

            // Set course hrefs for the student DTO based on enrolments
            studentDto.setCourseHrefs(student.getEnrollments().stream()
                    .map(Enrolment::getCourseHref)
                    .collect(Collectors.toList()));

            return studentDto;
        } catch (Exception e) {
            // Throw runtime exception if an internal error occurs during conversion
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Updates the details of a student based on the provided StudentDto object.
     *
     * @param studentDto The StudentDto object containing the updated student details.
     * @param authToken  The authentication token for authorization purposes.
     * @return ResponseEntity representing the updated StudentDto.
     * @throws LBUStudentsRuntimeException if there is an internal error during the process.
     */
    @Override
    public ResponseEntity<StudentDto> updateStudent(
            StudentDto studentDto,
            String authToken) {
        // Validate the provided StudentDto object
        validateStudentDto(studentDto);

        Student student;
        try {
            // Map the StudentDto object to a Student entity
            student = modelMapper.map(studentDto, Student.class);
        } catch (Exception e) {
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }

        // Update the student details in the database
        Student updatedStudent = studentService.updateStudentDetails(student);

        // Convert the updated Student entity to a StudentDto and return as ResponseEntity
        return ResponseEntity.ok(convertStudentDto(updatedStudent));
    }

    /**
     * Overrides the method to retrieve student details and return them as ResponseEntity.
     * Retrieves student details using the provided authentication user href and student ID.
     * Converts the retrieved student details into a StudentDto object.
     * @param studentId The ID of the student whose details are to be retrieved.
     * @param authToken The authentication token to validate the user.
     * @return ResponseEntity containing the StudentDto object with student details if successful.
     */
    @Override
    public ResponseEntity<StudentDto> getStudent(String authUserHref,
                                                 String studentId,
                                                 String authToken) {
        Student student = studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, studentId);
        return ResponseEntity.ok(convertStudentDto(student));
    }
}