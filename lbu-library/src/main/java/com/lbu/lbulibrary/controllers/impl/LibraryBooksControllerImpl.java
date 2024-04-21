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

    private void validateBookDto(BookDto bookDto) {
        if (Objects.isNull(bookDto.getAuthor())
                || Objects.isNull(bookDto.getCopies())
                || Objects.isNull(bookDto.getTitle())
                || Objects.isNull(bookDto.getYearOfPublished())) {
            throw new LBULibraryRuntimeException(BOOK__NOT_HAVING_REQUIRED.getErrorMessage(), BOOK__NOT_HAVING_REQUIRED.getErrorCode());
        }
    }

    @Override
    public ResponseEntity<BookDto> getBook(String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        try {
            return ResponseEntity.ok(modelMapper.map(book, BookDto.class));
        } catch (Exception e) {
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

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

    @Override
    public ResponseEntity<BookDto> updateBook(BookDto bookDto) {
        return null;
    }
}
