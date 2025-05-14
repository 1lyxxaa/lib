package ru.example.libraryclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Класс, представляющий книгу в клиентском приложении.
 * Содержит информацию о названии, годе издания, жанре, количестве страниц и авторе книги.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    private Long id;
    private String title;
    private Integer year;
    private String genre;
    private Integer pages;
    private Author author;
    private Boolean available;
    private String authorName;

    public Book() {}

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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
} 