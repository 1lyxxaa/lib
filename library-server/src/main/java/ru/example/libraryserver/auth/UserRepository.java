package ru.example.libraryserver.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет методы для поиска пользователей по логину.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
} 