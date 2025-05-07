package ru.example.libraryserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.libraryserver.model.Book;

/**
 * Репозиторий для работы с книгами.
 * Предоставляет методы для доступа к данным книг в базе данных.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
} 