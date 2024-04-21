package com.lbu.lbulibrary.service;

import com.lbu.lbulibrary.models.Book;

import java.util.List;

public interface BookService {
    Book createNewBook(Book book);

    List<Book> getBooks();

    Book getBookByIsbn(String isbn);

}
