package com.lbu.lbustudent.unittests;

import com.lbu.lbustudent.commons.constants.ErrorConstants;
import com.lbu.lbustudent.commons.exceptions.LBUStudentsRuntimeException;
import com.lbu.lbustudent.configuration.JsonTokenTestService;
import com.lbu.lbustudent.dtos.MessageDto;
import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import com.lbu.lbustudent.dtos.course.CourseDto;
import com.lbu.lbustudent.dtos.finance.FinanceAccountDto;
import com.lbu.lbustudent.dtos.finance.FinanceInvoiceDto;
import com.lbu.lbustudent.dtos.finance.Type;
import com.lbu.lbustudent.models.Student;
import com.lbu.lbustudent.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class StudentServiceTest extends StudentTestConfig {

    @Autowired
    private StudentService studentService;

    @Autowired
    private JsonTokenTestService jsonTokenTestService;

    @Qualifier("authRestTemplate")
    @MockBean
    private RestTemplate authRestTemplate;


    @Qualifier("courseRestTemplate")
    @MockBean
    private RestTemplate courseRestTemplate;

    @Qualifier("libraryRestTemplate")
    @MockBean
    private RestTemplate libraryRestTemplate;

    @Qualifier("financeRestTemplate")
    @MockBean
    private RestTemplate financeRestTemplate;

    /*create student*/
    @Test
    void testWhenCreateCourseEnrolment_ThenExisingStudentButCourseServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<Student> optionalStudent = studentRepository.findAll().stream().findAny();
        if (optionalStudent.isPresent()) {
            String courseHref = "/courses/" + UUID.randomUUID();
            Student student = optionalStudent.get();
            String authUserId = student.getAuthUserHref().split("/auth/user/")[1];
            String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
            LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                    () -> studentService.createStudentEnrolment(student.getAuthUserHref(), courseHref, authToken));
            Assertions.assertEquals("An error occurred in the course service.", exception.getMessage(), "Invalid exception message");
            Assertions.assertEquals(7001, exception.getCode(), "Invalid exception code");
        } else {
            Assertions.fail("No students are available");
        }
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenExisingStudentButFinanceServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<Student> optionalStudent = studentRepository.findAll().stream().findAny();
        if (optionalStudent.isPresent()) {
            String courseHref = "/courses/" + UUID.randomUUID();
            Student student = optionalStudent.get();
            String authUserId = student.getAuthUserHref().split("/auth/user/")[1];
            String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            CourseDto courseDto = new CourseDto();
            courseDto.setFees(new BigDecimal("100.00"));
            courseDto.setDurationInDays(5);

            when(courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class)).thenReturn(ResponseEntity.ok(courseDto));

            FinanceAccountDto financeAccountDto = new FinanceAccountDto();
            financeAccountDto.setAuthUserHref(student.getAuthUserHref());
            FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
            financeInvoiceDto.setAmount(courseDto.getFees().doubleValue());
            financeInvoiceDto.setType(Type.TUITION_FEES);
            financeInvoiceDto.setDueDate(LocalDate.now().plusDays(courseDto.getDurationInDays()));
            financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
            HttpEntity<FinanceAccountDto> requestEntityFinance = new HttpEntity<>(financeAccountDto, headers);
            when(financeRestTemplate.postForObject("/finance/account", requestEntityFinance, FinanceAccountDto.class)).thenThrow(new LBUStudentsRuntimeException(ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorMessage(), ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorCode()));

            LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                    () -> studentService.createStudentEnrolment(student.getAuthUserHref(), courseHref, authToken));
            Assertions.assertEquals("An error occurred in the finance service.", exception.getMessage(), "Invalid exception message");
            Assertions.assertEquals(7003, exception.getCode(), "Invalid exception code");
        } else {
            Assertions.fail("No students are available");
        }
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNewStudentButAuthServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("An error occurred in the auth service while upgrading the user.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(7000, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNewStudentButCourseServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String token = jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        String authToken = "Bearer " + token;


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        JWTTokenDto tokenDto = new JWTTokenDto();
        tokenDto.setJwtToken(token);
        tokenDto.setUserId(authUserId);

        when(authRestTemplate.postForObject(authUserHref, requestEntity, JWTTokenDto.class)).thenReturn(tokenDto);

        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("An error occurred in the course service.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(7001, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNewStudentButLibraryServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String token = jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        String authToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        JWTTokenDto tokenDto = new JWTTokenDto();
        tokenDto.setJwtToken(token);
        tokenDto.setUserId(authUserId);

        when(authRestTemplate.postForObject(authUserHref, requestEntity, JWTTokenDto.class)).thenReturn(tokenDto);

        CourseDto courseDto = new CourseDto();
        courseDto.setFees(new BigDecimal("100.00"));
        courseDto.setDurationInDays(5);

        when(courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class)).thenReturn(ResponseEntity.ok(courseDto));

        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("An error occurred in the library service.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(7002, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNewStudentButFinanceServiceNotWorking_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String token = jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        String authToken = "Bearer " + token;


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        JWTTokenDto tokenDto = new JWTTokenDto();
        tokenDto.setJwtToken(token);
        tokenDto.setUserId(authUserId);

        when(authRestTemplate.postForObject(authUserHref, requestEntity, JWTTokenDto.class)).thenReturn(tokenDto);

        CourseDto courseDto = new CourseDto();
        courseDto.setFees(new BigDecimal("100.00"));
        courseDto.setDurationInDays(5);

        when(courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class)).thenReturn(ResponseEntity.ok(courseDto));

        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("ssss");
        messageDto.setCode(24);
        when(libraryRestTemplate.exchange("/library/student", HttpMethod.POST, requestEntity, MessageDto.class)).thenReturn(ResponseEntity.ok(messageDto));

        FinanceAccountDto financeAccountDto = new FinanceAccountDto();
        financeAccountDto.setAuthUserHref(authUserHref);
        FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
        financeInvoiceDto.setAmount(courseDto.getFees().doubleValue());
        financeInvoiceDto.setType(Type.TUITION_FEES);
        financeInvoiceDto.setDueDate(LocalDate.now().plusDays(courseDto.getDurationInDays()));
        financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
        HttpEntity<FinanceAccountDto> requestEntityFinance = new HttpEntity<>(financeAccountDto, headers);
        when(financeRestTemplate.postForObject("/finance/account", requestEntityFinance, FinanceAccountDto.class)).thenThrow(new LBUStudentsRuntimeException(ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorMessage(), ErrorConstants.FINANCE_SERVICE_GET_ERROR.getErrorCode()));


        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("An error occurred in the finance service.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(7003, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenValidationErrorForCourseHref_ReturnCustomException() {
        String authUserHref = "/auth/user/" + UUID.randomUUID();
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, "testing", "testing"));
        Assertions.assertEquals("Invalid href provided.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(9002, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenValidationErrorForAuthUserHref_ReturnCustomException() {
        String courseHref = "/courses/" + UUID.randomUUID();
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment("testing", courseHref, "testing"));
        Assertions.assertEquals("Invalid href provided.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(9002, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNoBearerPrefix_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String authToken = jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("Invalid token.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(6006, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenValidationErrorExpired_ReturnCustomException() {
        String authUserHref = "/auth/user/" + UUID.randomUUID();
        String courseHref = "/courses/" + UUID.randomUUID();
        String authToken = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJudXcxOTkxIiwiaXNzIjoibGJ1LWF1dGgiLCJpYXQiOjE3MTMyMDYzNTcsImp0aSI6IjgyNjYxNjRjLWVhMWYtNDE5Zi05MjhiLTNiZjkxNDZkZDcwOCIsImV4cCI6MTcxMzI5Mjc1NywiZmlyc3ROYW1lIjoiTnV3YW4iLCJsYXN0TmFtZSI6Ik51d2FuIiwicm9sZXMiOiJST0xFX1NUVURFTlQiLCJ1c2VySWQiOiJhNWFlYTY3Ni00ZTVmLTRmZTItYmY2MC0xOTNjMzk4ZmE2MGEifQ.NyYsBPl80EbhpaTvqxu-NV84PXEoiuUzONUGzP0WNeA6VkMSvfIhd-e-lJJ6oyVGyhrKJBQ-Dv0qGnQ-mjf18F-KF7t3moswTVf5oynPhXixe97wRtM5VXfJ5zSdwBfFkoqRqJO8WR8-trUmTwlTHNBx0BEsMAecdsAm8l2kH7ZFpcu9v0ytiNYFKlPpm_RWImjFJ1FXDmERkeHB8A5d213pwcKaWgi9BvPSgzGa1lCKwqK6yjXQIrZYluYMdYLDedotmdboSlg9SsvchJCsMkLX0JXoekRd75oEfuQpBszYtTslexKWdJdFbznVEqnasi8mHlrFFVI1CiCPgnFXJw";
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("Given token is expired.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(6001, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenValidationErrorTokenMismatch_ReturnCustomException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String authUserIdAnother = UUID.randomUUID().toString();
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserIdAnother);
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.createStudentEnrolment(authUserHref, courseHref, authToken));
        Assertions.assertEquals("Invalid user access.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(6005, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenNewStudent_ReturnNewEnrolmentAndDetails() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String courseHref = "/courses/" + UUID.randomUUID();
        String token = jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        String authToken = "Bearer " + token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        JWTTokenDto tokenDto = new JWTTokenDto();
        tokenDto.setJwtToken(token);
        tokenDto.setUserId(authUserId);
        when(authRestTemplate.postForObject(authUserHref, requestEntity, JWTTokenDto.class)).thenReturn(tokenDto);

        CourseDto courseDto = new CourseDto();
        courseDto.setFees(new BigDecimal("100.00"));
        courseDto.setDurationInDays(5);
        when(courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class)).thenReturn(ResponseEntity.ok(courseDto));

        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("ssss");
        messageDto.setCode(24);
        when(libraryRestTemplate.exchange("/library/student", HttpMethod.POST, requestEntity, MessageDto.class)).thenReturn(ResponseEntity.ok(messageDto));

        FinanceAccountDto financeAccountDto = new FinanceAccountDto();
        financeAccountDto.setAuthUserHref(authUserHref);
        FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
        financeInvoiceDto.setAmount(courseDto.getFees().doubleValue());
        financeInvoiceDto.setType(Type.TUITION_FEES);
        financeInvoiceDto.setDueDate(LocalDate.now().plusDays(courseDto.getDurationInDays()));
        financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
        HttpEntity<FinanceAccountDto> requestEntityFinance = new HttpEntity<>(financeAccountDto, headers);
        when(financeRestTemplate.postForObject("/finance/account", requestEntityFinance, FinanceAccountDto.class)).thenReturn(financeAccountDto);

        Student student = studentService.createStudentEnrolment(authUserHref, courseHref, authToken);
        Assertions.assertTrue(Objects.nonNull(student.getJwtTokenDto()), "Not a new student");
        Assertions.assertFalse(student.getEnrollments().isEmpty(), "Enrolments were not created");
    }

    @Test
    void testWhenCreateCourseEnrolment_ThenStudentAlreadyExists_ReturnNewEnrolmentAndDetails() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<Student> optionalStudent = studentRepository.findAll().stream().findAny();
        if (optionalStudent.isPresent()) {
            String courseHref = "/courses/" + UUID.randomUUID();
            Student student = optionalStudent.get();
            String authUserId = student.getAuthUserHref().split("/auth/user/")[1];
            String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            CourseDto courseDto = new CourseDto();
            courseDto.setFees(new BigDecimal("100.00"));
            courseDto.setDurationInDays(5);
            when(courseRestTemplate.exchange(courseHref, HttpMethod.GET, requestEntity, CourseDto.class)).thenReturn(ResponseEntity.ok(courseDto));

            FinanceAccountDto financeAccountDto = new FinanceAccountDto();
            financeAccountDto.setAuthUserHref(student.getAuthUserHref());
            FinanceInvoiceDto financeInvoiceDto = new FinanceInvoiceDto();
            financeInvoiceDto.setAmount(courseDto.getFees().doubleValue());
            financeInvoiceDto.setType(Type.TUITION_FEES);
            financeInvoiceDto.setDueDate(LocalDate.now().plusDays(courseDto.getDurationInDays()));
            financeAccountDto.setInvoiceList(List.of(financeInvoiceDto));
            HttpEntity<FinanceAccountDto> requestEntityFinance = new HttpEntity<>(financeAccountDto, headers);
            when(financeRestTemplate.postForObject("/finance/account", requestEntityFinance, FinanceAccountDto.class)).thenReturn(financeAccountDto);

            Student studentUpdated = studentService.createStudentEnrolment(student.getAuthUserHref(), courseHref, authToken);
            Assertions.assertFalse(Objects.nonNull(studentUpdated.getJwtTokenDto()), "Not a new student");
            Assertions.assertFalse(studentUpdated.getEnrollments().isEmpty(), "Enrolments were not created");
        } else {
            Assertions.fail("No students are available");
        }
    }

    /*update student*/
    @Test
    void testWhenUpdateStudentDetails_ThenStudentNotAvailable_ReturnException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        Student student = new Student();
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        student.setAuthUserHref(authUserHref);
        student.setEmergencyContact("1223123");
        student.setAddress("test address");
        student.setDateOfBirth("1991");
        student.setPhoneContact("12231234");
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.updateStudentDetails(student, authToken));
        Assertions.assertEquals("Given student id is not available.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(10000, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenUpdateStudentDetails_ThenMissingMandatoryContent_ReturnException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        Student student = new Student();
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        student.setAuthUserHref(authUserHref);
        student.setAddress("test address");
        student.setDateOfBirth("1991");
        student.setPhoneContact("12231234");
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.updateStudentDetails(student, authToken));
        Assertions.assertEquals("Invalid course details provided.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(10001, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenUpdateStudentDetails_ThenValidationErrorMismatchToken_ReturnException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        Student student = new Student();
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", UUID.randomUUID().toString());
        student.setAuthUserHref(authUserHref);
        student.setEmergencyContact("1223123");
        student.setAddress("test address");
        student.setDateOfBirth("1991");
        student.setPhoneContact("12231234");
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.updateStudentDetails(student, authToken));
        Assertions.assertEquals("Invalid user access.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(6005, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenUpdateStudentDetails_ThenStudentUpdate_ReturnUpdatedStudent() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<Student> optionalStudent = studentRepository.findAll().stream().findAny();
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            String authUserId = existingStudent.getAuthUserHref().split("/auth/user/")[1];
            String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);

            existingStudent.setEmergencyContact("1223123");
            existingStudent.setAddress("test address");
            existingStudent.setDateOfBirth("1991");
            existingStudent.setPhoneContact("12231234");
            studentService.updateStudentDetails(existingStudent, authToken);
            Optional<Student> updatedStudentOptional = studentRepository.findByAuthUserHref(existingStudent.getAuthUserHref());
            if (updatedStudentOptional.isPresent()) {
                Student updatedStudent2 = updatedStudentOptional.get();
                Assertions.assertEquals("1223123", updatedStudent2.getEmergencyContact());
                Assertions.assertEquals("test address", updatedStudent2.getAddress());
            } else {
                Assertions.fail("No any student details");
            }
        } else {
            Assertions.fail("No students are available");
        }
    }

    /*get student*/

    @Test
    void testWhenGetStudentDetailsByAuthUserHref_ThenValidationErrorMismatchToken_ReturnException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", UUID.randomUUID().toString());
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, null, authToken));
        Assertions.assertEquals("Invalid user access.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(6005, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenGetStudentDetailsByAuthUserHref_ThenStudentNotAvailable_ReturnException() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String authUserId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + authUserId;
        String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
        LBUStudentsRuntimeException exception = Assertions.assertThrows(LBUStudentsRuntimeException.class,
                () -> studentService.getStudentDetailsFromAuthIdOrStudentId(authUserHref, null, authToken));
        Assertions.assertEquals("Given student id is not available.", exception.getMessage(), "Invalid exception message");
        Assertions.assertEquals(10000, exception.getCode(), "Invalid exception code");
    }

    @Test
    void testWhenGetStudentDetailsByAuthUserHref_ThenStudentAvailable_ReturnStudent() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<Student> optionalStudent = studentRepository.findAll().stream().findAny();
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            String authUserId = existingStudent.getAuthUserHref().split("/auth/user/")[1];
            String authToken = "Bearer " + jsonTokenTestService.getJwtToken("ROLE_STUDENT", authUserId);
            Student student = studentService.getStudentDetailsFromAuthIdOrStudentId(existingStudent.getAuthUserHref(), null, authToken);
            Assertions.assertTrue(Objects.nonNull(student), "Student object is available");
        } else {
            Assertions.fail("No students are available");
        }
    }
}
