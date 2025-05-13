package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.model.Reader;
import java.util.List;

public class ReaderService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private String authToken;

    public ReaderService(RestTemplate restTemplate, String baseUrl) {
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

    public List<Reader> getAllReaders() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<Reader>> response = restTemplate.exchange(
            baseUrl + "/api/readers",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Reader>>() {}
        );
        return response.getBody();
    }

    public Reader getReaderById(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Reader> response = restTemplate.exchange(
            baseUrl + "/api/readers/" + id,
            HttpMethod.GET,
            entity,
            Reader.class
        );
        return response.getBody();
    }

    public Reader createReader(Reader reader) {
        HttpEntity<Reader> entity = new HttpEntity<>(reader, createHeaders());
        ResponseEntity<Reader> response = restTemplate.exchange(
            baseUrl + "/api/readers",
            HttpMethod.POST,
            entity,
            Reader.class
        );
        return response.getBody();
    }

    public Reader updateReader(Reader reader) {
        HttpEntity<Reader> entity = new HttpEntity<>(reader, createHeaders());
        ResponseEntity<Reader> response = restTemplate.exchange(
            baseUrl + "/api/readers/" + reader.getId(),
            HttpMethod.PUT,
            entity,
            Reader.class
        );
        return response.getBody();
    }

    public void deleteReader(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(
            baseUrl + "/api/readers/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
    }
} 