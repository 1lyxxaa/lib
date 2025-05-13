package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.Book;
import java.util.List;

public class BookService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private String authToken;

    public BookService(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (authToken != null) {
            headers.set("X-Auth-Token", authToken);
        }
        return headers;
    }

    public List<Book> getAllBooks() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<Book>> response = restTemplate.exchange(
            baseUrl + "/api/books",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Book>>() {}
        );
        return response.getBody();
    }

    public Book getBookById(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Book> response = restTemplate.exchange(
            baseUrl + "/api/books/" + id,
            HttpMethod.GET,
            entity,
            Book.class
        );
        return response.getBody();
    }

    public Book createBook(Book book) {
        HttpEntity<Book> entity = new HttpEntity<>(book, createHeaders());
        ResponseEntity<Book> response = restTemplate.exchange(
            baseUrl + "/api/books",
            HttpMethod.POST,
            entity,
            Book.class
        );
        return response.getBody();
    }

    public Book updateBook(Book book) {
        HttpEntity<Book> entity = new HttpEntity<>(book, createHeaders());
        ResponseEntity<Book> response = restTemplate.exchange(
            baseUrl + "/api/books/" + book.getId(),
            HttpMethod.PUT,
            entity,
            Book.class
        );
        return response.getBody();
    }

    public void deleteBook(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(
            baseUrl + "/api/books/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
    }
} 