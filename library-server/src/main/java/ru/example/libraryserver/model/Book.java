package ru.example.libraryserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

    private Boolean available = true;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonBackReference
    private Author author;

    @OneToMany(mappedBy = "book")
    @JsonManagedReference("book-bookloan")
    private List<BookLoan> bookLoans;

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

    public List<BookLoan> getBookLoans() { return bookLoans; }
    public void setBookLoans(List<BookLoan> bookLoans) { this.bookLoans = bookLoans; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
} 