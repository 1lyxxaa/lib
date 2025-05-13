package ru.example.libraryserver.dto;

public class BookDto {
    private Long id;
    private String title;
    private String genre;
    private Integer year;
    private Integer pages;
    private Boolean available;
    private String authorName;

    public BookDto(Long id, String title, String genre, Integer year, Integer pages, Boolean available, String authorName) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.available = available;
        this.authorName = authorName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
} 