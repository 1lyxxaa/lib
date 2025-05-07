package ru.example.libraryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс серверного приложения библиотеки.
 * Запускает Spring Boot приложение.
 */
@SpringBootApplication
public class LibraryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryServerApplication.class, args);
    }
} 