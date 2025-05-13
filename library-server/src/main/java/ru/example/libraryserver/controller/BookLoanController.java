package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.BookLoan;
import ru.example.libraryserver.service.BookLoanService;
import ru.example.libraryserver.dto.BookLoanDto;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book-loans")
@CrossOrigin(origins = "http://localhost:8081")
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @Autowired
    public BookLoanController(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    private BookLoanDto toDto(BookLoan loan) {
        BookLoanDto dto = new BookLoanDto();
        dto.setId(loan.getId());
        dto.setBookTitle(loan.getBook() != null ? loan.getBook().getTitle() : null);
        dto.setReaderFullName(loan.getReader() != null ? loan.getReader().getFullName() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setOverdue(loan.isOverdue());
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<BookLoanDto>> getAllBookLoans() {
        return ResponseEntity.ok(bookLoanService.getAllBookLoans().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookLoanDto> getBookLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookLoanService.getBookLoanById(id)));
    }

    @PostMapping
    public ResponseEntity<BookLoanDto> createBookLoan(
            @RequestParam Long bookId,
            @RequestParam Long readerId,
            @RequestParam Long librarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(toDto(bookLoanService.createBookLoan(bookId, readerId, librarianId, dueDate)));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BookLoanDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookLoanService.returnBook(id)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BookLoanDto>> getOverdueBookLoans() {
        var overdueLoans = bookLoanService.getOverdueBookLoans();
        return ResponseEntity.ok(overdueLoans.stream().map(this::toDto).toList());
    }

    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<BookLoanDto>> getReaderBookLoans(@PathVariable Long readerId) {
        return ResponseEntity.ok(bookLoanService.getReaderBookLoans(readerId).stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/librarian/{librarianId}")
    public ResponseEntity<List<BookLoanDto>> getLibrarianBookLoans(@PathVariable Long librarianId) {
        return ResponseEntity.ok(bookLoanService.getLibrarianBookLoans(librarianId).stream().map(this::toDto).collect(Collectors.toList()));
    }
} 