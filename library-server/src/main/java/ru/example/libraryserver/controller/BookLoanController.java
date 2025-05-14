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
import ru.example.libraryserver.auth.RequireRole;

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
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public List<BookLoanDto> getAllBookLoans(@RequestHeader("X-Auth-Token") String token) {
        return bookLoanService.getAllBookLoans().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @RequireRole({"USER", "ADMIN"})
    public ResponseEntity<BookLoanDto> getBookLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookLoanService.getBookLoanById(id)));
    }

    @PostMapping
    @RequireRole({"ADMIN", "LIBRARIAN"})
    public ResponseEntity<BookLoanDto> createBookLoan(
            @RequestHeader("X-Auth-Token") String token,
            @RequestParam Long bookId,
            @RequestParam Long readerId,
            @RequestParam Long librarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(toDto(bookLoanService.createBookLoan(bookId, readerId, librarianId, dueDate)));
    }

    @PutMapping("/{id}/return")
    @RequireRole({"ADMIN", "LIBRARIAN"})
    public ResponseEntity<BookLoanDto> returnBook(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookLoanService.returnBook(id)));
    }

    @GetMapping("/overdue")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public List<BookLoanDto> getOverdueBookLoans(@RequestHeader("X-Auth-Token") String token) {
        var overdueLoans = bookLoanService.getOverdueBookLoans();
        return overdueLoans.stream().map(this::toDto).toList();
    }

    @GetMapping("/reader/{readerId}")
    @RequireRole({"USER", "ADMIN"})
    public ResponseEntity<List<BookLoanDto>> getReaderBookLoans(@PathVariable Long readerId) {
        return ResponseEntity.ok(bookLoanService.getReaderBookLoans(readerId).stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/librarian/{librarianId}")
    @RequireRole({"USER", "ADMIN"})
    public ResponseEntity<List<BookLoanDto>> getLibrarianBookLoans(@PathVariable Long librarianId) {
        return ResponseEntity.ok(bookLoanService.getLibrarianBookLoans(librarianId).stream().map(this::toDto).collect(Collectors.toList()));
    }
} 