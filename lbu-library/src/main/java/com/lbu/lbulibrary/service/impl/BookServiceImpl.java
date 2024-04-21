package com.lbu.lbulibrary.service.impl;

import com.lbu.lbulibrary.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbulibrary.models.Book;
import com.lbu.lbulibrary.repositories.BookRepository;
import com.lbu.lbulibrary.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lbu.lbulibrary.commons.constants.ErrorConstants.BOOK_NOT_AVAILABLE;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     Creates a new book entry in the database. This method initiates a transaction and rolls back any changes made
     in case of an Exception. It populates the ISBN for the book and saves the book entity using the bookRepository.
     @param book The book entity to be created.
     @return The created book entity.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Book createNewBook(Book book) {
        book.populateIsbn();
        return bookRepository.save(book);
    }

    /**
     Retrieves all books from the database. This method simply delegates the task to the bookRepository's findAll() method.
     @return A list of all books available in the database.
     */
    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    /**
     Retrieves a book by its ISBN from the database. This method queries the bookRepository for a book with the specified ISBN.
     If no book is found, it throws a LBULibraryRuntimeException with an appropriate error message.
     @param isbn The ISBN of the book to retrieve.
     @return The book entity corresponding to the provided ISBN.
     @throws LBULibraryRuntimeException if the book with the provided ISBN is not available in the database.
     */
    @Override
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findBookByIsbn(isbn)
                .orElseThrow(() -> new LBULibraryRuntimeException(BOOK_NOT_AVAILABLE.getErrorMessage(), BOOK_NOT_AVAILABLE.getErrorCode()));
    }


}
