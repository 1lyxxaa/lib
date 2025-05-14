package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.Author;
import ru.example.libraryserver.repository.AuthorRepository;
import ru.example.libraryserver.auth.RequireRole;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST-контроллер для работы с авторами.
 * Предоставляет методы для получения, создания, обновления и удаления авторов.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Возвращает список всех авторов.
     * @param token токен авторизации
     * @return список авторов
     */
    @GetMapping
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public List<ru.example.libraryserver.dto.AuthorDto> getAllAuthors(@RequestHeader("X-Auth-Token") String token) {
        return authorRepository.findAll().stream()
            .map(a -> new ru.example.libraryserver.dto.AuthorDto(a.getId(), a.getName(), a.getBirthYear()))
            .toList();
    }

    /**
     * Возвращает автора по его идентификатору.
     * @param token токен авторизации
     * @param id идентификатор автора
     * @return ResponseEntity с автором или ошибкой
     */
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public ResponseEntity<Author> getAuthorById(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Создает нового автора.
     * @param token токен авторизации
     * @param author данные автора
     * @return созданный автор
     */
    @PostMapping
    @RequireRole("ADMIN")
    public Author createAuthor(@RequestHeader("X-Auth-Token") String token, @Valid @RequestBody Author author) {
        return authorRepository.save(author);
    }

    /**
     * Обновляет существующего автора.
     * @param token токен авторизации
     * @param id идентификатор автора
     * @param authorDetails новые данные автора
     * @return ResponseEntity с обновленным автором или ошибкой
     */
    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Author> updateAuthor(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @Valid @RequestBody Author authorDetails) {
        return authorRepository.findById(id)
                .map(author -> {
                    author.setName(authorDetails.getName());
                    author.setBirthYear(authorDetails.getBirthYear());
                    return ResponseEntity.ok(authorRepository.save(author));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Удаляет автора по его идентификатору.
     * @param token токен авторизации
     * @param id идентификатор автора
     * @return ResponseEntity с пустым телом или ошибкой
     */
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<Void> deleteAuthor(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 