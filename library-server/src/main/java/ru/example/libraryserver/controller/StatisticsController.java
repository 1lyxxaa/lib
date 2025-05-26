package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.dto.StatisticsDto;
import ru.example.libraryserver.repository.BookRepository;
import ru.example.libraryserver.repository.ReaderRepository;
import ru.example.libraryserver.repository.BookLoanRepository;
import ru.example.libraryserver.model.Book;
import ru.example.libraryserver.auth.RequireRole;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private BookLoanRepository bookLoanRepository;

    @GetMapping
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public StatisticsDto getStatistics(@RequestHeader("X-Auth-Token") String token) {
        StatisticsDto dto = new StatisticsDto();
        dto.setTotalBooks(bookRepository.count());
        dto.setTotalReaders(readerRepository.count());
        dto.setTotalLoans(bookLoanRepository.count());
        Map<String, Long> booksByGenre = bookRepository.findAll().stream()
            .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        dto.setBooksByGenre(booksByGenre);
        return dto;
    }

    @GetMapping("/readers")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public Map<String, Long> getReaderLoanStats(@RequestHeader("X-Auth-Token") String token) {
        return bookLoanRepository.findAll().stream()
            .filter(bl -> bl.getReader() != null)
            .collect(Collectors.groupingBy(
                bl -> bl.getReader().getFullName(),
                Collectors.counting()
            ));
    }
} 