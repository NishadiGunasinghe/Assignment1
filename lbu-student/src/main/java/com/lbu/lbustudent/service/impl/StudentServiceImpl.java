package com.lbu.lbustudent.service.impl;

import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.auth.services.AuthService;
import com.lbu.lbustudent.commons.externalservices.course.services.CourseService;
import com.lbu.lbustudent.commons.externalservices.finance.services.FinanceService;
import com.lbu.lbustudent.commons.externalservices.library.service.LibraryService;
import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import com.lbu.lbustudent.dtos.course.CourseDto;
import com.lbu.lbustudent.models.Enrollment;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.repositories.StudentRepository;
import com.lbu.lbustudent.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.*;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AuthService authService;
    private final FinanceService financeService;
    private final CourseService courseService;
    private final LibraryService libraryService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AuthService authService,
                              FinanceService financeService,
                              CourseService courseService,
                              LibraryService libraryService) {
        this.studentRepository = studentRepository;
        this.authService = authService;
        this.financeService = financeService;
        this.courseService = courseService;
        this.libraryService = libraryService;
    }

    /**
     * Creates a student enrolment in the system. Validates the provided user and course HREFs, and the authentication token.
     * If the student already exists, adds course details to their enrolment and updates related services accordingly.
     * If the student is new, creates a new student record, updates authentication status, and sets up related services.
     * Handles database access exceptions and rolls back the transaction in case of any exception.
     *
     * @param authUserHref The HREF of the authenticated user.
     * @param courseHref   The HREF of the course to enrol the student in.
     * @param token        The authentication token.
     * @return The created or updated student record.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Student createStudentEnrolment(String authUserHref, String courseHref, String token) {
        validateHrefAndGetId(courseHref);
        validateHrefAndGetId(authUserHref);
        authService.validateAuthUserHref(authUserHref, token);
        try {
            Optional<Student> optionalStudent = studentRepository.findByAuthUserHref(authUserHref);
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                log.info("Existing student and we are adding the course details into the enrolment {}", student.getId());
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourseHref(courseHref);
                student.getEnrollments().add(enrollment);
                CourseDto courseDto = courseService.getCourseDetails(courseHref, token);
                financeService.createOrUpdateFinanceAccount(courseDto, authUserHref, token);
                return studentRepository.saveAndFlush(student);
            } else {
                log.info("Student is not available and this is first enrolment {}", authUserHref);
                Student student = new Student();
                student.setAuthUserHref(authUserHref);
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourseHref(courseHref);
                student.setEnrollments(List.of(enrollment));
                Student savedStudent = studentRepository.saveAndFlush(student);
                log.info("student creation success student id{} auth id {}", student.getId(), student.getAuthUserHref());
                JWTTokenDto jwtTokenDto;
                try {
                    jwtTokenDto = authService.updateUserStatus(authUserHref, token);
                    savedStudent.setJwtTokenDto(jwtTokenDto);
                    log.info("Auth user updated to STUDENT {}", jwtTokenDto.getUserId());
                } catch (Exception e) {
                    throw new LBUStudentsRuntimeException(AUTH_SERVICE_USER_UPDATE_ERROR.getErrorMessage(), AUTH_SERVICE_USER_UPDATE_ERROR.getErrorCode(), e);
                }
                CourseDto courseDto = courseService.getCourseDetails(courseHref, "Bearer " + jwtTokenDto.getJwtToken());
                libraryService.createLibraryAccount("Bearer " + jwtTokenDto.getJwtToken());
                financeService.createOrUpdateFinanceAccount(courseDto, authUserHref, "Bearer " + jwtTokenDto.getJwtToken());
                return savedStudent;
            }
        } catch (DataAccessException e) {
            log.info("An error occurred while calling the database", e);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Updates details of an existing student in the system. Validates the provided student object and authentication token.
     * If the student exists, updates their details and saves the changes to the database.
     * Throws an exception if the student is not found.
     * Handles database access exceptions and rolls back the transaction in case of any exception.
     *
     * @param student   The student object containing updated details.
     * @param authToken The authentication token.
     * @return The updated student record.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Student updateStudentDetails(Student student, String authToken) {
        validateStudent(student);
        authService.validateAuthUserHref(student.getAuthUserHref(), authToken);
        try {
            Optional<Student> optionalStudent = studentRepository.findByAuthUserHref(student.getAuthUserHref());
            if (optionalStudent.isPresent()) {
                Student existingStudent = optionalStudent.get();
                existingStudent.setEmergencyContact(student.getEmergencyContact());
                existingStudent.setAddress(student.getAddress());
                existingStudent.setDateOfBirth(student.getDateOfBirth());
                existingStudent.setPhoneContact(student.getPhoneContact());
                log.info("Updating the student details {}", existingStudent);
                return studentRepository.save(existingStudent);
            } else {
                throw new LBUStudentsRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode());
            }
        } catch (DataAccessException e) {
            log.info("An error occurred while calling the database", e);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Retrieves student details either by auth user HREF or student ID, validating the authentication token.
     * If authUserHref is provided, retrieves the student by authUserHref.
     * If studentId is provided, retrieves the student by studentId.
     * Throws an exception if neither authUserHref nor studentId is provided or if the student is not found.
     * Handles database access exceptions.
     *
     * @param authUserHref The HREF of the authenticated user.
     * @param studentId    The ID of the student.
     * @param authToken    The authentication token.
     * @return The student details.
     */
    @Override
    public Student getStudentDetailsFromAuthIdOrStudentId(String authUserHref, String studentId, String authToken) {
        authService.validateAuthUserHref(authUserHref, authToken);
        try {
            if (Objects.nonNull(authUserHref)) {
                Optional<Student> studentOptional = studentRepository.findByAuthUserHref(authUserHref);
                if (studentOptional.isPresent()) {
                    return studentOptional.get();
                } else {
                    throw new LBUStudentsRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode());
                }
            } else if (Objects.nonNull(studentId)) {
                Optional<Student> studentOptional = studentRepository.findById(studentId);
                if (studentOptional.isPresent()) {
                    return studentOptional.get();
                } else {
                    throw new LBUStudentsRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode());
                }
            } else {
                throw new LBUStudentsRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode());
            }
        } catch (DataAccessException e) {
            log.info("An error occurred while calling the database", e);
            throw new LBUStudentsRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }
}
