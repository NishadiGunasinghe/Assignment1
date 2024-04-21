package com.lbu.lbu_library.controllers.impl;

import com.lbu.lbu_library.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbu_library.commons.externalservices.auth.services.AuthService;
import com.lbu.lbu_library.controllers.LibraryBooksController;
import com.lbu.lbu_library.dtos.BookDto;
import com.lbu.lbu_library.dtos.BookDtos;
import com.lbu.lbu_library.models.Book;
import com.lbu.lbu_library.models.Transaction;
import com.lbu.lbu_library.service.BookFineService;
import com.lbu.lbu_library.service.BookService;
import com.lbu.lbu_library.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK__NOT_HAVING_REQUIRED;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class LibraryBooksControllerImpl implements LibraryBooksController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final TransactionService transactionService;

    private final BookFineService bookFineService;

    /**
     * Constructor for LibraryBooksControllerImpl.
     *
     * @param bookService       The service for book-related operations.
     * @param modelMapper       The mapper for converting between DTOs and entities.
     * @param authService       The service for authentication-related operations.
     * @param transactionService The service for transaction-related operations.
     * @param bookFineService   The service for book fine-related operations.
     */
    public LibraryBooksControllerImpl(BookService bookService,
                                      ModelMapper modelMapper,
                                      AuthService authService,
                                      TransactionService transactionService,
                                      BookFineService bookFineService) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.transactionService = transactionService;
        this.bookFineService = bookFineService;
    }

    /**
     * Creates a new book.
     *
     * @param bookDto The DTO representing the book to be created.
     * @return A ResponseEntity containing the created book DTO.
     */
    @Override
    public ResponseEntity<BookDto> createBook(BookDto bookDto) {
        validateBookDto(bookDto);
        Book book;
        try {
            book = modelMapper.map(bookDto, Book.class);
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Book savedBook = bookService.createNewBook(book);
        try {
            return ResponseEntity.ok(modelMapper.map(savedBook, BookDto.class));
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Validates a book DTO.
     *
     * @param bookDto The DTO representing the book to be validated.
     */
    private void validateBookDto(BookDto bookDto) {
        if (Objects.isNull(bookDto.getAuthor())
                || Objects.isNull(bookDto.getCopies())
                || Objects.isNull(bookDto.getTitle())
                || Objects.isNull(bookDto.getYearOfPublished())) {
            throw new LBULibraryRuntimeException(BOOK__NOT_HAVING_REQUIRED.getErrorMessage(), BOOK__NOT_HAVING_REQUIRED.getErrorCode());
        }
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn The ISBN of the book to retrieve.
     * @return A ResponseEntity containing the book DTO.
     */
    @Override
    public ResponseEntity<BookDto> getBook(String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        try {
            return ResponseEntity.ok(modelMapper.map(book, BookDto.class));
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Retrieves all books.
     *
     * @param token The authentication token.
     * @return A ResponseEntity containing a list of book DTOs.
     */
    @Override
    public ResponseEntity<BookDtos> getBooks(String token) {
        List<Transaction> transactions = null;
        try {
            String authUserHref = authService.validateAuthUserHref(token);
            transactions = transactionService.getTransactionsByAuthUserHref(authUserHref);
            bookFineService.checkForBookFines(token, authUserHref);
        } catch (LBULibraryRuntimeException e) {
            log.error("no data available for the user");
        }
        List<Book> books = bookService.getBooks();
        try {
            BookDtos bookDtos = new BookDtos();
            List<BookDto> list = new ArrayList<>();
            for (Book book : books) {
                BookDto dto = modelMapper.map(book, BookDto.class);
                if (Objects.nonNull(transactions)) {
                    if (transactions.stream()
                            .anyMatch(transaction -> transaction.getBook().getIsbn().equals(dto.getIsbn()) && Objects.isNull(transaction.getDateReturned()))) {
                        dto.setIsBorrowed(Boolean.TRUE);
                    } else {
                        dto.setIsBorrowed(Boolean.FALSE);
                    }
                }
                list.add(dto);
            }
            bookDtos.setBooks(list);
            return ResponseEntity.ok(bookDtos);
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Updates a book.
     *
     * @param bookDto The DTO representing the book to be updated.
     * @return A ResponseEntity containing the updated book DTO.
     */
    @Override
    public ResponseEntity<BookDto> updateBook(BookDto bookDto) {
        return null;
    }

}
