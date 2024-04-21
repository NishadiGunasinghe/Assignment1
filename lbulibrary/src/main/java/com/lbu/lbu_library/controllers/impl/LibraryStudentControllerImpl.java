package com.lbu.lbu_library.controllers.impl;

import com.lbu.lbu_library.commons.exceptions.LBULibraryRuntimeException;
import com.lbu.lbu_library.commons.externalservices.auth.services.AuthService;
import com.lbu.lbu_library.controllers.LibraryStudentController;
import com.lbu.lbu_library.dtos.BookDto;
import com.lbu.lbu_library.dtos.MessageDto;
import com.lbu.lbu_library.dtos.StudentDto;
import com.lbu.lbu_library.dtos.TransactionDto;
import com.lbu.lbu_library.models.Student;
import com.lbu.lbu_library.service.BookFineService;
import com.lbu.lbu_library.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.lbu.lbu_library.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class LibraryStudentControllerImpl implements LibraryStudentController {

    private final AuthService authService;
    private final StudentService studentService;
    private final ModelMapper modelMapper;
    private final BookFineService bookFineService;

    /**
     * Constructs a LibraryStudentControllerImpl with necessary services and dependencies.
     *
     * @param authService     The authentication service.
     * @param studentService  The student service.
     * @param modelMapper     The model mapper for DTO conversion.
     * @param bookFineService The service for handling book fines.
     */
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
     * Creates a new student entity associated with the authenticated user.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing a message indicating success or failure.
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
     * Retrieves student information for the authenticated user.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing the student information.
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
     * Allows the authenticated user to borrow a book.
     *
     * @param isbn  The ISBN of the book to borrow.
     * @param token The authentication token.
     * @return ResponseEntity containing a message indicating success or failure.
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
     * Allows the authenticated user to return a book.
     *
     * @param isbn  The ISBN of the book to return.
     * @param token The authentication token.
     * @return ResponseEntity containing a message indicating success or failure.
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
