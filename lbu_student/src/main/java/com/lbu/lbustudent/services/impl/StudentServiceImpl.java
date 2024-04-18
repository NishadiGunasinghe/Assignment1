package com.lbu.lbustudent.services.impl;

import com.lbu.lbustudent.commons.exeptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.commons.externalservices.auth.services.AuthService;
import com.lbu.lbustudent.commons.externalservices.course.services.CourseService;
import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import com.lbu.lbustudent.dtos.course.CourseDto;
import com.lbu.lbustudent.models.Enrolment;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.repositories.StudentRepository;
import com.lbu.lbustudent.services.StudentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.lbu.lbustudent.commons.constants.ErrorConstants.AUTH_SERVICE_USER_UPDATE_ERROR;
import static com.lbu.lbustudent.commons.constants.ErrorConstants.STUDENT_NOT_AVAILABLE;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService{

    private final StudentRepository studentRepository;
    private final CourseService courseService;
    private final AuthService authService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AuthService authService,
                              CourseService courseService) {
        this.studentRepository = studentRepository;
        this.authService = authService;
        this.courseService = courseService;
    }

    /**
     * Creates a student enrolment for the provided authentication user and course.
     *
     * @param authUserHref The href of the authentication user.
     * @param courseHref   The href of the course.
     * @param token        The authentication token.
     * @return The created or updated student object with the new enrolment.
     */
    @Transactional(rollbackOn = Exception.class) // Ensures transactional integrity
    @Override
    public Student createStudentEnrolment(String authUserHref, String courseHref, String token) {
        Optional<Student> optionalStudent = studentRepository.findByAuthUserHref(authUserHref);
        if (optionalStudent.isPresent()) { // If student exists, add enrolment.
            Student student = optionalStudent.get();
            Enrolment enrollment = new Enrolment(); // Create new enrolment.
            enrollment.setStudent(student); // Associate enrolment with student.
            enrollment.setCourseHref(courseHref); // Set the course href.
            student.getEnrollments().add(enrollment); // Add enrolment to student's enrolment list.
            CourseDto courseDto = courseService.getCourseDetails(courseHref);// Fetch course details.
            return studentRepository.saveAndFlush(student); // Save and flush changes to database.
        } else { // If student does not exist, create new student and enrolment.
            Student student = new Student(); // Create new student.
            student.setAuthUserHref(authUserHref); // Set authentication user href.
            Enrolment enrollment = new Enrolment(); // Create new enrolment.
            enrollment.setStudent(student); // Associate enrolment with student.
            enrollment.setCourseHref(courseHref); // Set the course href.
            student.setEnrollments(List.of(enrollment)); // Set student's enrolments.
            Student savedStudent = studentRepository.saveAndFlush(student); // Save and flush changes to database.
            JWTTokenDto jwtTokenDto;
            try {
                jwtTokenDto = authService.updateUserStatus(authUserHref, token);
                savedStudent.setJwtTokenDto(jwtTokenDto);
                log.info("Auth user updated to STUDENT {}", jwtTokenDto.getUserId());
            } catch (Exception e) {
                throw new LBUStudentsRuntimeException(AUTH_SERVICE_USER_UPDATE_ERROR.getErrorMessage(), AUTH_SERVICE_USER_UPDATE_ERROR.getErrorCode());
            }
            CourseDto courseDto = courseService.getCourseDetails(courseHref); // Fetch course details.
            return savedStudent; // Return the saved student.
        }
    }

    /**
     * Updates the details of a student.
     *
     * @param student The Student object containing the updated details.
     * @return Returns a Student object after updating its details, or null if no update was performed.
     */
    @Override
    public Student updateStudentDetails(Student student) {
        return null;
    }

    @Override
    public Student getStudentDetailsFromAuthIdOrStudentId(String authUserHref, String studentId) {
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
    }
}
