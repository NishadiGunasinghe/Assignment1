package com.lbu.lbu_library.service.impl;

import com.lbu.lbu_library.models.Book;
import com.lbu.lbu_library.models.Student;
import com.lbu.lbu_library.models.Transaction;
import com.lbu.lbu_library.repositories.BookRepository;
import com.lbu.lbu_library.repositories.StudentRepository;
import com.lbu.lbu_library.repositories.TransactionRepository;
import com.lbu.lbu_library.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.lbu.lbu_library.commons.exceptions.LBULibraryRuntimeException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK_ALREADY_BORROWED;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK_ALREADY_RETURNED;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK_NOT_AVAILABLE;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.BOOK_NOT_BORROWED;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.INTERNAL_ERROR;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.MAX_BOOK_ALREADY_BORROWED;
import static com.lbu.lbu_library.commons.constants.ErrorConstants.STUDENT_NOT_AVAILABLE;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    private final TransactionRepository transactionRepository;


    /**
     * Initializes StudentServiceImpl with required repositories.
     *
     * @param studentRepository    The repository for managing student entities.
     * @param bookRepository       The repository for managing book entities.
     * @param transactionRepository The repository for managing transaction entities.
     */
    public StudentServiceImpl(StudentRepository studentRepository,
                              BookRepository bookRepository,
                              TransactionRepository transactionRepository) {
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new student.
     *
     * @param student The student object to be created.
     * @throws LBULibraryRuntimeException If an error occurs during the creation process.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void createNewStudent(Student student) {
        try {
            Student save = studentRepository.save(student);
            log.info("Student created {}", save.getId());
        } catch (Exception e) {
            log.error("An error occurred while creating the student", e);
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Retrieves a student by their authentication user href.
     *
     * @param authUserHref The href of the authentication user associated with the student.
     * @return The retrieved student object.
     * @throws LBULibraryRuntimeException If the student is not available.
     */
    @Override
    public Student getStudentByAuthUserHref(String authUserHref) {
        Optional<Student> studentOptional = studentRepository.findStudentByAuthUserHref(authUserHref);
        return studentOptional
                .orElseThrow(() -> new LBULibraryRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode()));
    }

    /**
     * Allows a student to borrow a book.
     *
     * @param isbn         The ISBN of the book to be borrowed.
     * @param authUserHref The href of the authentication user borrowing the book.
     * @throws LBULibraryRuntimeException If the book is not available, already borrowed, or maximum books already borrowed.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void borrowBook(String isbn, String authUserHref) {
        Student student = getStudent(authUserHref);
        log.info("Student details found {}", student);
        Book book = bookRepository.findBookByIsbn(isbn)
                .orElseThrow(() -> new LBULibraryRuntimeException(BOOK_NOT_AVAILABLE.getErrorMessage(), BOOK_NOT_AVAILABLE.getErrorCode()));
        log.info("Book details found {}", book);
        List<Transaction> transactionForAuthUserId = transactionRepository.findAllByStudent_AuthUserHrefAndDateReturnedIsNull(authUserHref);
        if (transactionForAuthUserId.stream().anyMatch(transaction -> transaction.getBook().getIsbn().equals(book.getIsbn()) && Objects.isNull(transaction.getDateReturned()))) {
            log.info("given student already borrowed the given book {}", book);
            throw new LBULibraryRuntimeException(BOOK_ALREADY_BORROWED.getErrorMessage(), BOOK_ALREADY_BORROWED.getErrorCode());
        }
        long noOfBooksBorrowedAlready = transactionRepository.findAllByBook_IsbnAndDateReturnedIsNull(book.getIsbn())
                .stream().filter(transaction -> Objects.isNull(transaction.getDateReturned())).count();
        if (book.getCopies() > noOfBooksBorrowedAlready) {
            Transaction transaction = new Transaction();
            transaction.setBook(book);
            transaction.setStudent(student);
            transaction.setDateBorrowed(Timestamp.from(Instant.now()));
            Transaction save = transactionRepository.save(transaction);
            log.info("New book borrowed {}", save);
        } else {
            log.info("all the books are borrowed please try again later");
            throw new LBULibraryRuntimeException(MAX_BOOK_ALREADY_BORROWED.getErrorMessage(), MAX_BOOK_ALREADY_BORROWED.getErrorCode());
        }
    }

    /**
     * Allows a student to return a book.
     *
     * @param isbn         The ISBN of the book to be returned.
     * @param authUserHref The href of the authentication user returning the book.
     * @throws LBULibraryRuntimeException If the book is not available, already returned, or not borrowed.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void returnBook(String isbn, String authUserHref) {
        Book book = bookRepository.findBookByIsbn(isbn)
                .orElseThrow(() -> new LBULibraryRuntimeException(BOOK_NOT_AVAILABLE.getErrorMessage(), BOOK_NOT_AVAILABLE.getErrorCode()));
        log.info("Book details found {}", book);
        Optional<Transaction> optionalTransaction = transactionRepository.findAllByStudent_AuthUserHrefAndDateReturnedIsNull(authUserHref)
                .stream()
                .filter(transaction -> transaction.getBook().getIsbn().equals(book.getIsbn()))
                .findAny();
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            if (Objects.nonNull(transaction.getDateReturned())) {
                throw new LBULibraryRuntimeException(BOOK_ALREADY_RETURNED.getErrorMessage(), BOOK_ALREADY_RETURNED.getErrorCode());
            }
            transaction.setDateReturned(Timestamp.from(Instant.now()));
            Transaction save = transactionRepository.save(transaction);
            log.info("Book returned {}", save);
        } else {
            log.info("given student not borrowed the given book {}", book);
            throw new LBULibraryRuntimeException(BOOK_NOT_BORROWED.getErrorMessage(), BOOK_NOT_BORROWED.getErrorCode());
        }
    }

    /**
     * Retrieves a student by their authentication user href.
     *
     * @param authUserHref The href of the authentication user associated with the student.
     * @return The retrieved student object.
     * @throws LBULibraryRuntimeException If the student is not available.
     */
    private Student getStudent(String authUserHref) {
        return studentRepository.findStudentByAuthUserHref(authUserHref)
                .orElseThrow(() -> new LBULibraryRuntimeException(STUDENT_NOT_AVAILABLE.getErrorMessage(), STUDENT_NOT_AVAILABLE.getErrorCode()));
    }

}
