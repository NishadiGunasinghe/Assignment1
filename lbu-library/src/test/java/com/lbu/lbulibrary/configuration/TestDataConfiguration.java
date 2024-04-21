package com.lbu.lbulibrary.configuration;

import com.lbu.lbulibrary.models.Book;
import com.lbu.lbulibrary.models.Student;
import com.lbu.lbulibrary.models.Transaction;
import com.lbu.lbulibrary.repositories.BookRepository;
import com.lbu.lbulibrary.repositories.StudentRepository;
import com.lbu.lbulibrary.repositories.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class TestDataConfiguration {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${custom.properties.seconds.bookreturn}")
    private Integer secondsReturn;

    @PostConstruct
    public void init() {
        List<Book> books = saveBooks();
        List<Student> students = saveStudents();
        log.info("student details {}", students);
        createTransactions(books, students);
    }

    private void createTransactions(List<Book> books, List<Student> students) {
        Optional<Student> studentOptional = students.stream().findFirst();
        if (studentOptional.isPresent() && !books.isEmpty()) {
            Student student = studentOptional.get();
            List<Transaction> transactions = books.stream().map(book -> {
                Transaction transaction = new Transaction();
                transaction.setBook(book);
                transaction.setStudent(student);
                transaction.setDateBorrowed(Timestamp.from(Instant.now().minusSeconds(secondsReturn)));
                return transaction;
            }).collect(Collectors.toList());
            transactionRepository.saveAll(transactions);
        }
    }

    private List<Student> saveStudents() {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            Student student = new Student();
            student.setAuthUserHref("/auth/user/" + UUID.randomUUID());
            students.add(student);
        }
        return studentRepository.saveAll(students);
    }

    private List<Book> saveBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            Book book = new Book();
            book.setAuthor("Book Author " + i);
            book.populateIsbn();
            book.setCopies(i);
            book.setTitle("Book Title " + i);
            book.setYearOfPublished(Integer.parseInt("200" + i));
            books.add(book);
        }
        return bookRepository.saveAll(books);
    }

}
