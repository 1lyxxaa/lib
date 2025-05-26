package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.example.libraryserver.model.Book;
import ru.example.libraryserver.model.Author;
import ru.example.libraryserver.repository.BookRepository;
import ru.example.libraryserver.repository.AuthorRepository;
import ru.example.libraryserver.auth.RequireRole;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST-контроллер для работы со статистикой.
 * Предоставляет методы для получения статистики по жанрам, страницам и авторам.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Возвращает статистику по жанрам книг.
     * @param token токен авторизации
     * @return карта с количеством книг по жанрам
     */
    @GetMapping("/genres")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public Map<String, Long> booksByGenre(@RequestHeader("X-Auth-Token") String token) {
        return bookRepository.findAll().stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    /**
     * Возвращает статистику по страницам книг.
     * @param token токен авторизации
     * @return карта с минимальным, максимальным, средним количеством страниц и общим количеством книг
     */
    @GetMapping("/pages")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public Map<String, Number> pagesStats(@RequestHeader("X-Auth-Token") String token) {
        List<Book> books = bookRepository.findAll();
        IntSummaryStatistics stats = books.stream()
                .filter(b -> b.getPages() != null)
                .mapToInt(Book::getPages)
                .summaryStatistics();
        Map<String, Number> result = new HashMap<>();
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        result.put("avg", stats.getAverage());
        result.put("count", stats.getCount());
        return result;
    }

    /**
     * Возвращает статистику по авторам книг.
     * @param token токен авторизации
     * @return карта с количеством книг по авторам
     */
    @GetMapping("/authors")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public Map<String, Long> booksByAuthor(@RequestHeader("X-Auth-Token") String token) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getAuthor() != null)
                .collect(Collectors.groupingBy(b -> b.getAuthor().getName(), Collectors.counting()));
    }
} 