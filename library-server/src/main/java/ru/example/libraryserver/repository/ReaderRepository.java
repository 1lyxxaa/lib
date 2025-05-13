package ru.example.libraryserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.libraryserver.model.Reader;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
} 