package com.lbu.lbu_library.service;

import com.lbu.lbu_library.models.Book;

import java.util.List;

public interface BookService {
    Book createNewBook(Book book);

    List<Book> getBooks();

    Book getBookByIsbn(String isbn);

}
