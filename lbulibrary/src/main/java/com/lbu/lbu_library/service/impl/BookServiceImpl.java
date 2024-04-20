package com.lbu.lbu_library.service.impl;

import com.lbu.lbu_library.models.Book;
import com.lbu.lbu_library.repositories.BookRepository;
import com.lbu.lbu_library.service.BookService;
import jakarta.transaction.Transactional;
import com.lbu.lbu_library.commons.exceptions.LBULibraryRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK_NOT_AVAILABLE;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    /**
     * Constructs a BookServiceImpl with the provided BookRepository dependency.
     *
     * @param bookRepository The repository for managing Book entities.
     */
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Creates a new book entity.
     *
     * @param book The book entity to be created.
     * @return The created book entity.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Book createNewBook(Book book) {
        book.populateIsbn();
        return bookRepository.save(book);
    }

    /**
     * Retrieves all books from the repository.
     *
     * @return A list of all books.
     */
    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn The ISBN of the book to retrieve.
     * @return The book corresponding to the provided ISBN.
     * @throws LBULibraryRuntimeException If the book with the provided ISBN is not found.
     */
    @Override
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findBookByIsbn(isbn)
                .orElseThrow(() -> new LBULibraryRuntimeException(BOOK_NOT_AVAILABLE.getErrorMessage(), BOOK_NOT_AVAILABLE.getErrorCode()));
    }

}
