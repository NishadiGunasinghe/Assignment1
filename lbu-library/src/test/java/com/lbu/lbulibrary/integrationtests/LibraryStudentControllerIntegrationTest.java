package com.lbu.lbulibrary.integrationtests;

import com.lbu.lbulibrary.LbuLibraryApplicationTests;
import com.lbu.lbulibrary.models.Book;
import com.lbu.lbulibrary.models.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;

public class LibraryStudentControllerIntegrationTest extends LbuLibraryApplicationTests {

    /*create the student*/
    @Test
    public void testWhenWithoutHeader_ThenTryCreateStudent_ReturnError() throws Exception {
        testWithoutHeaders(null, "/library/student", HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidToken_ThenCreateStudent_ReturnErrorMessage() throws Exception {
        testInvalidAuthenticationToken(null, "/library/student", HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenCreateStudent_ReturnErrorMessage() throws Exception {
        testInvalidAccessLevel(null, "/library/student", HttpMethod.POST);
    }

    @Test
    public void testWhenBookAdd_ThenCreateStudent_ReturnSuccessMessage() throws Exception {

    }

    /*@Test
    public void testWhenSameStudentDetailsProvided_ThenTryCreateStudent_ReturnErrorMessage() throws Exception {
        Student studentFromDb = getAnyStudentFromDb();
        String token = withAuthenticationWithStudent();
        when(authService.validateAuthUserHref(token)).thenReturn(studentFromDb.getAuthUserHref());
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/library/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MessageDto messageDto = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), MessageDto.class);

        System.out.println();
    }*/

    /*get the student*/
    @Test
    public void testWhenWithoutHeader_ThenTryGetStudent_ReturnError() throws Exception {

    }

    @Test
    public void testWhenWithInvalidToken_ThenTryGetStudent_ReturnErrorMessage() throws Exception {

    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenTryGetStudent_ReturnErrorMessage() throws Exception {

    }

    @Test
    public void testWhenGetStudentDetails_ThenTryGetStudent_ReturnSuccessMessage() throws Exception {

    }

    /*borrow a book by student*/
    @Test
    public void testWhenWithoutHeader_ThenTryBorrowBook_ReturnError() throws Exception {
        Book book = getAnyBookFromDb();
        testWithoutHeaders(null, "/library/student/borrow/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidToken_ThenTryBorrowBook_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAuthenticationToken(null, "/library/student/borrow/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenTryBorrowBook_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAccessLevel(null, "/library/student/borrow/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenBorrowBook_ThenTryBorrowBook_ReturnSuccessMessage() throws Exception {

    }

    /*return a book by student*/
    @Test
    public void testWhenWithoutHeader_ThenTryReturnBook_ReturnError() throws Exception {
        Book book = getAnyBookFromDb();
        testWithoutHeaders(null, "/library/student/return/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidToken_ThenTryReturnBook_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAuthenticationToken(null, "/library/student/return/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenTryReturnBook_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAccessLevel(null, "/library/student/return/" + book.getIsbn(), HttpMethod.POST);
    }

    @Test
    public void testWhenReturnBook_ThenTryReturnBook_ReturnSuccessMessage() throws Exception {

    }

    private Book getAnyBookFromDb() {
        List<Book> books = bookRepository.findAll();
        Assertions.assertFalse(books.isEmpty(), "Books are empty");
        return books.get(0);
    }

    private Student getAnyStudentFromDb() {
        List<Student> students = studentRepository.findAll();
        Assertions.assertFalse(students.isEmpty(), "Students are empty");
        return students.get(0);
    }
}
