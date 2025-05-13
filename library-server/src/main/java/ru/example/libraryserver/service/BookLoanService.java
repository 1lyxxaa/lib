package ru.example.libraryserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.libraryserver.model.Book;
import ru.example.libraryserver.model.BookLoan;
import ru.example.libraryserver.model.Reader;
import ru.example.libraryserver.model.User;
import ru.example.libraryserver.repository.BookLoanRepository;
import ru.example.libraryserver.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final ReaderService readerService;
    private final UserService userService;

    @Autowired
    public BookLoanService(BookLoanRepository bookLoanRepository,
                          BookRepository bookRepository,
                          BookService bookService,
                          ReaderService readerService,
                          UserService userService) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.readerService = readerService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<BookLoan> getAllBookLoans() {
        return bookLoanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BookLoan getBookLoanById(Long id) {
        return bookLoanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Выдача книги не найдена"));
    }

    @Transactional
    public BookLoan createBookLoan(Long bookId, Long readerId, Long librarianId, LocalDate dueDate) {
        Book book = bookService.getBookById(bookId);
        if (!book.getAvailable()) {
            throw new IllegalStateException("Книга уже выдана");
        }

        Reader reader = readerService.getReaderById(readerId);
        User librarian = userService.getUserById(librarianId);

        BookLoan bookLoan = new BookLoan();
        bookLoan.setBook(book);
        bookLoan.setReader(reader);
        bookLoan.setLibrarian(librarian);
        bookLoan.setLoanDate(LocalDate.now());
        bookLoan.setDueDate(dueDate);
        bookLoan.setOverdue(false);

        book.setAvailable(false);
        bookRepository.save(book);

        return bookLoanRepository.save(bookLoan);
    }

    @Transactional
    public BookLoan returnBook(Long bookLoanId) {
        BookLoan bookLoan = getBookLoanById(bookLoanId);
        if (bookLoan.getReturnDate() != null) {
            throw new IllegalStateException("Книга уже возвращена");
        }

        bookLoan.setReturnDate(LocalDate.now());

        Book book = bookLoan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return bookLoanRepository.save(bookLoan);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> getOverdueBookLoans() {
        return bookLoanRepository.findAll().stream()
            .filter(BookLoan::isOverdue)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<BookLoan> getReaderBookLoans(Long readerId) {
        return bookLoanRepository.findByReaderId(readerId);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> getLibrarianBookLoans(Long librarianId) {
        return bookLoanRepository.findByLibrarianId(librarianId);
    }
} 