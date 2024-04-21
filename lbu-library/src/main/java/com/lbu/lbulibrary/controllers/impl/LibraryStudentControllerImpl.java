package com.lbu.lbulibrary.controllers.impl;

import com.lbu.lbulibrary.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbulibrary.commons.externalservices.auth.services.AuthService;
import com.lbu.lbulibrary.controllers.LibraryStudentController;
import com.lbu.lbulibrary.dtos.BookDto;
import com.lbu.lbulibrary.dtos.MessageDto;
import com.lbu.lbulibrary.dtos.StudentDto;
import com.lbu.lbulibrary.dtos.TransactionDto;
import com.lbu.lbulibrary.models.Student;
import com.lbu.lbulibrary.service.BookFineService;
import com.lbu.lbulibrary.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.lbu.lbulibrary.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class LibraryStudentControllerImpl implements LibraryStudentController {

    private final AuthService authService;
    private final StudentService studentService;
    private final ModelMapper modelMapper;
    private final BookFineService bookFineService;

    public LibraryStudentControllerImpl(AuthService authService,
                                        StudentService studentService,
                                        ModelMapper modelMapper,
                                        BookFineService bookFineService) {
        this.authService = authService;
        this.studentService = studentService;
        this.modelMapper = modelMapper;
        this.bookFineService = bookFineService;
    }

    /**
     * Validates the user's authentication token, creates a new student with the authenticated user's href,
     * and saves the student. It then constructs a message indicating successful creation of the student
     * and returns it as a ResponseEntity with HTTP status 200.
     *
     * @param token The authentication token of the user.
     * @return ResponseEntity containing a message indicating successful creation of the student.
     */
    @Override
    public ResponseEntity<MessageDto> createStudent(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        Student student = new Student();
        student.setAuthUserHref(authUserHref);
        studentService.createNewStudent(student);
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(200);
        messageDto.setMessage("Student created");
        return ResponseEntity.ok(messageDto);
    }

    /**
     * Validates the user's authentication token, checks for any book fines associated with the user,
     * retrieves the student details based on the authenticated user's href, constructs a StudentDto
     * with borrowed book details, and returns it as a ResponseEntity with HTTP status 200.
     * If any error occurs during model conversion, it is logged and rethrown as an LBULibraryRuntimeException.
     *
     * @param token The authentication token of the user.
     * @return ResponseEntity containing the details of the student.
     */
    @Override
    public ResponseEntity<StudentDto> getStudent(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        bookFineService.checkForBookFines(token, authUserHref);
        Student student = studentService.getStudentByAuthUserHref(authUserHref);
        log.info("Found student {}", student);
        try {
            StudentDto studentDto = modelMapper.map(student, StudentDto.class);
            studentDto.setBorrowedBooks(student.getTransactions().stream().map(transaction -> {
                TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);
                transactionDto.setBook(modelMapper.map(transaction.getBook(), BookDto.class));
                return transactionDto;
            }).collect(Collectors.toList()));
            return ResponseEntity.ok(studentDto);
        } catch (Exception e) {
            log.error("Model conversion error {}", student);
            throw new LBULibraryRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Validates the user's authentication token, checks for any book fines associated with the user,
     * and borrows a book for the authenticated user based on the provided ISBN.
     * It then constructs a message indicating successful book borrowing and returns it
     * as a ResponseEntity with HTTP status 200.
     *
     * @param isbn  The ISBN of the book to borrow.
     * @param token The authentication token of the user.
     * @return ResponseEntity containing a message indicating successful book borrowing.
     */
    @Override
    public ResponseEntity<MessageDto> borrowBook(String isbn, String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        bookFineService.checkForBookFines(token, authUserHref);
        studentService.borrowBook(isbn, authUserHref);
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(200);
        messageDto.setMessage("Book borrowed");
        return ResponseEntity.ok(messageDto);
    }

    /**
     * Validates the user's authentication token, checks for any book fines associated with the user,
     * and returns a borrowed book for the authenticated user based on the provided ISBN.
     * It then constructs a message indicating successful book return and returns it
     * as a ResponseEntity with HTTP status 200.
     *
     * @param isbn  The ISBN of the book to return.
     * @param token The authentication token of the user.
     * @return ResponseEntity containing a message indicating successful book return.
     */
    @Override
    public ResponseEntity<MessageDto> returnBook(String isbn, String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        bookFineService.checkForBookFines(token, authUserHref);
        studentService.returnBook(isbn, authUserHref);
        MessageDto messageDto = new MessageDto();
        messageDto.setCode(200);
        messageDto.setMessage("Book returned");
        return ResponseEntity.ok(messageDto);
    }
}
