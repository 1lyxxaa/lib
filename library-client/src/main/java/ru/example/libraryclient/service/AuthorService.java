package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.Author;
import java.util.List;

public class AuthorService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private String authToken;

    public AuthorService(RestTemplate restTemplate, String baseUrl) {
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

    public List<Author> getAllAuthors() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<Author>> response = restTemplate.exchange(
            baseUrl + "/api/authors",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Author>>() {}
        );
        return response.getBody();
    }

    public Author getAuthorById(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Author> response = restTemplate.exchange(
            baseUrl + "/api/authors/" + id,
            HttpMethod.GET,
            entity,
            Author.class
        );
        return response.getBody();
    }

    public Author createAuthor(Author author) {
        HttpEntity<Author> entity = new HttpEntity<>(author, createHeaders());
        ResponseEntity<Author> response = restTemplate.exchange(
            baseUrl + "/api/authors",
            HttpMethod.POST,
            entity,
            Author.class
        );
        return response.getBody();
    }

    public Author updateAuthor(Author author) {
        HttpEntity<Author> entity = new HttpEntity<>(author, createHeaders());
        ResponseEntity<Author> response = restTemplate.exchange(
            baseUrl + "/api/authors/" + author.getId(),
            HttpMethod.PUT,
            entity,
            Author.class
        );
        return response.getBody();
    }

    public void deleteAuthor(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(
            baseUrl + "/api/authors/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
    }
} 