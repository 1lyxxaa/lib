package ru.example.libraryserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Класс, представляющий книгу в библиотеке.
 * Содержит информацию о названии, годе издания, жанре, количестве страниц и авторе книги.
 */
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название книги не может быть пустым")
    @Size(max = 200, message = "Название книги не должно превышать 200 символов")
    private String title;

    @NotNull(message = "Год издания обязателен")
    private Integer year;

    @NotBlank(message = "Жанр обязателен")
    @Size(max = 50, message = "Жанр не должен превышать 50 символов")
    private String genre;

    @NotNull(message = "Количество страниц обязательно")
    private Integer pages;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
} 