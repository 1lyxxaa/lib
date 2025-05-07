package ru.example.libraryserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.libraryserver.model.Author;

/**
 * Репозиторий для работы с авторами.
 * Предоставляет методы для доступа к данным авторов в базе данных.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
} 