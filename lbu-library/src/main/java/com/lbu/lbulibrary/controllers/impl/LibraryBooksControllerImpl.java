package com.lbu.lbulibrary.controllers.impl;

import com.lbu.lbulibrary.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbulibrary.commons.externalservices.auth.services.AuthService;
import com.lbu.lbulibrary.controllers.LibraryBooksController;
import com.lbu.lbulibrary.dtos.BookDto;
import com.lbu.lbulibrary.dtos.BookDtos;
import com.lbu.lbulibrary.models.Book;
import com.lbu.lbulibrary.models.Transaction;
import com.lbu.lbulibrary.service.BookFineService;
import com.lbu.lbulibrary.service.BookService;
import com.lbu.lbulibrary.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lbu.lbulibrary.commons.constants.ErrorConstants.BOOK__NOT_HAVING_REQUIRED;
import static com.lbu.lbulibrary.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class LibraryBooksControllerImpl implements LibraryBooksController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final TransactionService transactionService;

    private final BookFineService bookFineService;

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
     * Creates a new book entry based on the provided BookDto. It validates the BookDto, maps it to a Book entity using
     * ModelMapper, and then saves the new book. If any exception occurs during mapping or saving, it is caught and rethrown
     * as an LBULibraryRuntimeException.
     *
     * @param bookDto The BookDto object containing information about the book to be created.
     * @return ResponseEntity containing the newly created BookDto upon successful creation.
     */
    @Override
    public ResponseEntity<BookDto> createBook(BookDto bookDto) {
        // Method to validate the BookDto
        validateBookDto(bookDto);
        // Attempt to map the BookDto to a Book entity
        Book book;
        try {
            book = modelMapper.map(bookDto, Book.class);
        } catch (Exception e) {
            // If an exception occurs during mapping, throw LBULibraryRuntimeException
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        // Save the newly created book
        Book savedBook = bookService.createNewBook(book);
        // Attempt to map the saved Book entity back to a BookDto
        try {
            return ResponseEntity.ok(modelMapper.map(savedBook, BookDto.class));
        } catch (Exception e) {
            // If an exception occurs during mapping, throw LBULibraryRuntimeException
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Validates the provided BookDto object. It checks if essential fields like author, title, copies, and year of
     * publication are not null. If any of these fields are null, it throws an LBULibraryRuntimeException.
     *
     * @param bookDto The BookDto object to be validated.
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
     * Retrieves a book based on the provided ISBN. It fetches the book from the database using the BookService and then
     * maps it to a BookDto. If any exception occurs during mapping, it is caught and rethrown as an LBULibraryRuntimeException.
     *
     * @param isbn The ISBN of the book to be retrieved.
     * @return ResponseEntity containing the retrieved BookDto upon successful retrieval.
     */
    @Override
    public ResponseEntity<BookDto> getBook(String isbn) {
        // Retrieve the book from the database using the ISBN
        Book book = bookService.getBookByIsbn(isbn);
        // Attempt to map the retrieved Book entity to a BookDto
        try {
            return ResponseEntity.ok(modelMapper.map(book, BookDto.class));
        } catch (Exception e) {
            // If an exception occurs during mapping, throw LBULibraryRuntimeException
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Retrieves all books and their borrowing status associated with a user based on the provided token. It retrieves
     * transactions associated with the user, checks for any book fines, and then fetches all books from the database.
     * Finally, it maps each book to a BookDto and sets its borrowing status. If any exception occurs during this process,
     * it is caught and rethrown as an LBULibraryRuntimeException.
     *
     * @param token The authentication token of the user.
     * @return ResponseEntity containing a list of BookDtos representing all books and their borrowing status.
     */
    @Override
    public ResponseEntity<BookDtos> getBooks(String token) {
        // Initialize list of transactions
        List<Transaction> transactions = null;
        try {
            // Validate the user authentication token and retrieve associated transactions
            String authUserHref = authService.validateAuthUserHref(token);
            transactions = transactionService.getTransactionsByAuthUserHref(authUserHref);
            // Check for any book fines associated with the user
            bookFineService.checkForBookFines(token, authUserHref);
        } catch (LBULibraryRuntimeException e) {
            // Log error if no data available for the user
            log.error("No data available for the user");
        }
        // Retrieve all books from the database
        List<Book> books = bookService.getBooks();
        try {
            // Initialize BookDtos object to hold list of BookDto
            BookDtos bookDtos = new BookDtos();
            List<BookDto> list = new ArrayList<>();
            // Iterate through each book and map it to a BookDto
            for (Book book : books) {
                BookDto dto = modelMapper.map(book, BookDto.class);
                // Set borrowing status based on associated transactions
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
            // Set list of books in the BookDtos object and return ResponseEntity
            bookDtos.setBooks(list);
            return ResponseEntity.ok(bookDtos);
        } catch (Exception e) {
            // If an exception occurs during mapping, throw LBULibraryRuntimeException
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Updates an existing book entry based on the provided BookDto. This method is not implemented yet and returns null.
     *
     * @param bookDto The BookDto object containing updated information about the book.
     * @return Currently returns null as the method is not implemented.
     */
    @Override
    public ResponseEntity<BookDto> updateBook(BookDto bookDto) {
        return null;
    }

}
