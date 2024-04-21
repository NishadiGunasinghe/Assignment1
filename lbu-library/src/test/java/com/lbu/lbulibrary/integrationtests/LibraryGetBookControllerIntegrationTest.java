package com.lbu.lbulibrary.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbu.lbulibrary.LbuLibraryApplicationTests;
import com.lbu.lbulibrary.dtos.BookDto;
import com.lbu.lbulibrary.dtos.BookDtos;
import com.lbu.lbulibrary.models.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

public class LibraryGetBookControllerIntegrationTest extends LbuLibraryApplicationTests {

    /*create the books*/
    @Test
    public void testWhenWithoutHeader_ThenTryCreateBook_ReturnError() throws Exception {
        testWithoutHeaders(getBookDto("Author Test", 1, "Title Test", 2023), "/library/books", HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidToken_ThenCreateBook_ReturnErrorMessage() throws Exception {
        testInvalidAuthenticationToken(getBookDto("Author Test", 1, "Title Test", 2023), "/library/books", HttpMethod.POST);
    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenCreateBook_ReturnErrorMessage() throws Exception {
        testInvalidAccessLevel(getBookDto("Author Test", 1, "Title Test", 2023), "/library/books", HttpMethod.POST);
    }

    @Test
    public void testWhenCreateBooks_ThenCreateBook_ReturnBookDetails() throws Exception {
        String token = withAuthenticationWithAdmin();
        BookDto bookDto = getBookDto("Author Test", 1, "Title Test", 2023);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(asJsonString(bookDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BookDto bookDtoSaved = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(bookDto.getAuthor(), bookDtoSaved.getAuthor(), "Book author is invalid");
        Assertions.assertEquals(bookDto.getTitle(), bookDtoSaved.getTitle(), "Book title is invalid");
        Assertions.assertEquals(bookDto.getYearOfPublished(), bookDtoSaved.getYearOfPublished(), "Book year of published is invalid");
        Assertions.assertTrue(Objects.nonNull(bookDtoSaved.getIsbn()), "Book does not have isbn");
    }

    /*get books for isbn*/

    @Test
    public void testWhenWithoutHeaderForIsbnGet_ThenTryGet_ReturnError() throws Exception {
        Book book = getAnyBookFromDb();
        testWithoutHeaders(null, "/library/books/" + book.getIsbn(), HttpMethod.GET);
    }

    @Test
    public void testWhenWithInvalidTokenForIsbnGet_ThenGet_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAuthenticationToken(null, "/library/books/" + book.getIsbn(), HttpMethod.GET);
    }

    @Test
    public void testWhenWithInvalidAccessLevelForIsbnGet_ThenGet_ReturnErrorMessage() throws Exception {
        Book book = getAnyBookFromDb();
        testInvalidAccessLevel(null, "/library/books/" + book.getIsbn(), HttpMethod.GET);
    }

    @Test
    public void testWhenGetBooksForIsbnGet_ThenGet_ReturnListOfBooks() throws Exception {
        Book book = getAnyBookFromDb();
        String token = withAuthenticationWithAdmin();
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/library/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BookDto bookDetails = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(book.getAuthor(), bookDetails.getAuthor(), "Book author is invalid");
        Assertions.assertEquals(book.getTitle(), bookDetails.getTitle(), "Book title is invalid");
        Assertions.assertEquals(book.getYearOfPublished(), bookDetails.getYearOfPublished(), "Book year of published is invalid");
        Assertions.assertEquals(book.getIsbn(), bookDetails.getIsbn(), "Book ISBN is invalid");
    }

    /*get all the books*/
    @Test
    public void testWhenWithoutHeader_ThenTryGet_ReturnError() throws Exception {
        testWithoutHeaders(null, "/library/books", HttpMethod.GET);
    }

    @Test
    public void testWhenWithInvalidToken_ThenGet_ReturnErrorMessage() throws Exception {
        testInvalidAuthenticationToken(null, "/library/books", HttpMethod.GET);
    }

    @Test
    public void testWhenWithInvalidAccessLevel_ThenGet_ReturnErrorMessage() throws Exception {
        testInvalidAuthenticationToken(null, "/library/books", HttpMethod.GET);
    }

    @Test
    public void testWhenGetBooks_ThenGet_ReturnListOfBooks() throws Exception {
        List<Book> books = bookRepository.findAll();
        String token = withAuthenticationWithAdmin();
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
        BookDtos bookDetails = new ObjectMapper()
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), BookDtos.class);
        Assertions.assertEquals(books.size(), bookDetails.getBooks().size(), "Books are not equal");
        Book book = books.get(0);
        BookDto bookDto = bookDetails.getBooks().get(0);
        Assertions.assertEquals(book.getAuthor(), bookDto.getAuthor(), "Book author is invalid");
        Assertions.assertEquals(book.getTitle(), bookDto.getTitle(), "Book title is invalid");
        Assertions.assertEquals(book.getYearOfPublished(), bookDto.getYearOfPublished(), "Book year of published is invalid");
        Assertions.assertEquals(book.getIsbn(), bookDto.getIsbn(), "Book ISBN is invalid");
    }

    private static BookDto getBookDto(String author, Integer copyCount, String title, Integer yearOfPublished) {
        BookDto dto = new BookDto();
        dto.setAuthor(author);
        dto.setCopies(copyCount);
        dto.setTitle(title);
        dto.setYearOfPublished(yearOfPublished);
        return dto;
    }

    private Book getAnyBookFromDb() {
        List<Book> books = bookRepository.findAll();
        Assertions.assertFalse(books.isEmpty(), "Books are empty");
        return books.get(0);
    }
}
