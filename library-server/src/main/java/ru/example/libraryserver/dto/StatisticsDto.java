package ru.example.libraryserver.dto;

import java.util.Map;

public class StatisticsDto {
    private long totalBooks;
    private long totalReaders;
    private long totalLoans;
    private Map<String, Long> booksByGenre;

    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
    public long getTotalReaders() { return totalReaders; }
    public void setTotalReaders(long totalReaders) { this.totalReaders = totalReaders; }
    public long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(long totalLoans) { this.totalLoans = totalLoans; }
    public Map<String, Long> getBooksByGenre() { return booksByGenre; }
    public void setBooksByGenre(Map<String, Long> booksByGenre) { this.booksByGenre = booksByGenre; }
} 