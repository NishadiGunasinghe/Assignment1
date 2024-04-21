package com.lbu.lbustudent.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbu.lbustudent.dtos.MessageDto;
import com.lbu.lbustudent.dtos.StudentDto;
import com.lbu.lbustudent.dtos.StudentEnrolmentDto;
import com.lbu.lbustudent.dtos.auth.JWTTokenDto;
import com.lbu.lbustudent.dtos.course.CourseDto;
import com.lbu.lbustudent.models.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class StudentIntegrationTest extends StudentIntegrationTestConfig {

    /*enrolling for a subject or create a student while enrolling*/
    @Test
    public void testWhenNewEnrolmentForExistingStudentWithoutHeader_ThenTryCreateStudent_ReturnError() throws Exception {
        Student existingStudent = getAnExistingStudent();
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID());
        studentEnrolmentDto.setAuthUserHref(existingStudent.getAuthUserHref());
        testWithoutHeaders(studentEnrolmentDto, "/student/enrolment", HttpMethod.POST);
    }

    @Test
    public void testWhenNewEnrolmentForExistingStudentWithInvalidToken_ThenCreateStudent_ReturnErrorMessage() throws Exception {
        Student existingStudent = getAnExistingStudent();
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID());
        studentEnrolmentDto.setAuthUserHref(existingStudent.getAuthUserHref());
        testInvalidAuthenticationToken(studentEnrolmentDto, "/student/enrolment", HttpMethod.POST);
    }

    /*@Test
    public void testWhenNewEnrolmentForExistingStudentWithInvalidAccessLevel_ThenCreateStudent_ReturnErrorMessage() throws Exception {
        Student existingStudent = getAnExistingStudent();
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID());
        studentEnrolmentDto.setAuthUserHref(existingStudent.getAuthUserHref());
        testInvalidAccessLevel(studentEnrolmentDto, "/student/enrolment", HttpMethod.POST);
    }*/


    @Test
    public void testWhenNewEnrolmentForExistingStudent_ThenValidating_ReturnError() throws Exception {
        Student existingStudent = getAnExistingStudent();
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID() + "gg");
        studentEnrolmentDto.setAuthUserHref(existingStudent.getAuthUserHref());
        String token = withAuthenticationWithStudent();
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/student/enrolment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(asJsonString(studentEnrolmentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        MessageDto messageDto = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), MessageDto.class);
        Assertions.assertEquals("Invalid href provided.", messageDto.getMessage(), "Invalid message");
        Assertions.assertEquals(9002, messageDto.getCode(), "Invalid code");
    }

    @Test
    public void testWhenNewEnrolmentForExistingStudent_ThenAddingEnrolment_ReturnStudentDetailsWithAllEnrolments() throws Exception {
        Student existingStudent = getAnExistingStudent();
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID());
        studentEnrolmentDto.setAuthUserHref(existingStudent.getAuthUserHref());
        String token = withAuthenticationWithStudent();
        CourseDto courseDto = new CourseDto();
        courseDto.setIdHref(studentEnrolmentDto.getCourseHref());
        courseDto.setFees(new BigDecimal("123.0"));
        courseDto.setTitle("Testing");
        courseDto.setDurationInDays(10);
        when(courseService.getCourseDetails(studentEnrolmentDto.getCourseHref(), token)).thenReturn(courseDto);
        doNothing().when(financeService).createOrUpdateFinanceAccount(courseDto, studentEnrolmentDto.getAuthUserHref(), token);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/student/enrolment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(asJsonString(studentEnrolmentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        StudentDto studentDto = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), StudentDto.class);
        Assertions.assertEquals(existingStudent.getEnrollments().size() + 1, studentDto.getCourseHrefs().size(), "No courses enrolled");
    }

    @Test
    public void testWhenNewEnrolmentForNewStudent_ThenAddingEnrolment_ReturnStudentDetailsWithAllEnrolments() throws Exception {
        StudentEnrolmentDto studentEnrolmentDto = new StudentEnrolmentDto();
        String userId = UUID.randomUUID().toString();
        String authUserHref = "/auth/user/" + userId;
        studentEnrolmentDto.setCourseHref("/courses/" + UUID.randomUUID());
        studentEnrolmentDto.setAuthUserHref(authUserHref);
        String token = withAuthenticationWithStudent();
        CourseDto courseDto = new CourseDto();
        courseDto.setIdHref(studentEnrolmentDto.getCourseHref());
        courseDto.setFees(new BigDecimal("123.0"));
        courseDto.setTitle("Testing");
        courseDto.setDurationInDays(10);
        /*mocking external services*/
        when(courseService.getCourseDetails(studentEnrolmentDto.getCourseHref(), token)).thenReturn(courseDto);
        doNothing().when(financeService).createOrUpdateFinanceAccount(courseDto, authUserHref, token);
        JWTTokenDto jwtTokenDto = new JWTTokenDto();
        jwtTokenDto.setUserId(userId);
        jwtTokenDto.setJwtToken(token);
        when(authService.updateUserStatus(any(), any())).thenReturn(jwtTokenDto);
        when(libraryService.createLibraryAccount("Bearer " + token)).thenReturn(Boolean.TRUE);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/student/enrolment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(asJsonString(studentEnrolmentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        StudentDto studentDto = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), StudentDto.class);

        Assertions.assertEquals(studentEnrolmentDto.getCourseHref(), studentDto.getCourseHrefs().get(0), "No courses enrolled");
    }


}
