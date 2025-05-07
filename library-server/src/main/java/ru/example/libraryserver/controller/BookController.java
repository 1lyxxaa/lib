package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.Book;
import ru.example.libraryserver.model.Author;
import ru.example.libraryserver.repository.BookRepository;
import ru.example.libraryserver.repository.AuthorRepository;
import jakarta.validation.Valid;
import ru.example.libraryserver.auth.RequireRole;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

/**
 * REST-контроллер для работы с книгами.
 * Предоставляет методы для получения, создания, обновления и удаления книг, а также для поиска книг по различным критериям.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Возвращает список всех книг.
     * @param token токен авторизации
     * @return список книг
     */
    @GetMapping
    @RequireRole("USER")
    public List<Book> getAllBooks(@RequestHeader("X-Auth-Token") String token) {
        return bookRepository.findAll();
    }

    /**
     * Возвращает книгу по ее идентификатору.
     * @param token токен авторизации
     * @param id идентификатор книги
     * @return ResponseEntity с книгой или ошибкой
     */
    @GetMapping("/{id}")
    @RequireRole("USER")
    public ResponseEntity<Book> getBookById(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Создает новую книгу.
     * @param token токен авторизации
     * @param book данные книги
     * @return ResponseEntity с созданной книгой или ошибкой
     */
    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<Book> createBook(@RequestHeader("X-Auth-Token") String token, @Valid @RequestBody Book book) {
        // Проверяем, что автор существует
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            Optional<Author> author = authorRepository.findById(book.getAuthor().getId());
            if (author.isPresent()) {
                book.setAuthor(author.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(bookRepository.save(book));
    }

    /**
     * Обновляет существующую книгу.
     * @param token токен авторизации
     * @param id идентификатор книги
     * @param bookDetails новые данные книги
     * @return ResponseEntity с обновленной книгой или ошибкой
     */
    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Book> updateBook(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(bookDetails.getTitle());
                    book.setYear(bookDetails.getYear());
                    book.setGenre(bookDetails.getGenre());
                    book.setPages(bookDetails.getPages());
                    if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getId() != null) {
                        Optional<Author> author = authorRepository.findById(bookDetails.getAuthor().getId());
                        author.ifPresent(book::setAuthor);
                    }
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Удаляет книгу по ее идентификатору.
     * @param token токен авторизации
     * @param id идентификатор книги
     * @return ResponseEntity с пустым телом или ошибкой
     */
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Void> deleteBook(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ищет книги по различным критериям.
     * @param token токен авторизации
     * @param genre жанр книги
     * @param title название книги
     * @param authorId идентификатор автора
     * @param yearFrom год издания (от)
     * @param yearTo год издания (до)
     * @param pagesFrom количество страниц (от)
     * @param pagesTo количество страниц (до)
     * @param sort поле для сортировки
     * @param order порядок сортировки (asc/desc)
     * @return список книг, соответствующих критериям поиска
     */
    @GetMapping("/search")
    @RequireRole("USER")
    public List<Book> searchBooks(@RequestHeader("X-Auth-Token") String token,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer pagesFrom,
            @RequestParam(required = false) Integer pagesTo,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        List<Book> books = bookRepository.findAll();
        // Фильтрация
        if (genre != null && !genre.isEmpty()) {
            books.removeIf(b -> !genre.equalsIgnoreCase(b.getGenre()));
        }
        if (title != null && !title.isEmpty()) {
            books.removeIf(b -> !b.getTitle().toLowerCase().contains(title.toLowerCase()));
        }
        if (authorId != null) {
            books.removeIf(b -> b.getAuthor() == null || !authorId.equals(b.getAuthor().getId()));
        }
        if (yearFrom != null) {
            books.removeIf(b -> b.getYear() == null || b.getYear() < yearFrom);
        }
        if (yearTo != null) {
            books.removeIf(b -> b.getYear() == null || b.getYear() > yearTo);
        }
        if (pagesFrom != null) {
            books.removeIf(b -> b.getPages() == null || b.getPages() < pagesFrom);
        }
        if (pagesTo != null) {
            books.removeIf(b -> b.getPages() == null || b.getPages() > pagesTo);
        }
        // Сортировка
        books.sort((b1, b2) -> {
            int cmp = 0;
            switch (sort) {
                case "title": cmp = b1.getTitle().compareToIgnoreCase(b2.getTitle()); break;
                case "year": cmp = Integer.compare(
                        b1.getYear() != null ? b1.getYear() : 0,
                        b2.getYear() != null ? b2.getYear() : 0); break;
                case "pages": cmp = Integer.compare(
                        b1.getPages() != null ? b1.getPages() : 0,
                        b2.getPages() != null ? b2.getPages() : 0); break;
                case "genre": cmp = b1.getGenre().compareToIgnoreCase(b2.getGenre()); break;
                default: cmp = Long.compare(
                        b1.getId() != null ? b1.getId() : 0L,
                        b2.getId() != null ? b2.getId() : 0L);
            }
            return "desc".equalsIgnoreCase(order) ? -cmp : cmp;
        });
        return books;
    }
} 