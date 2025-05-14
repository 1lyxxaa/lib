package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.model.Reader;
import ru.example.libraryclient.dto.ReaderDto;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ReaderDto> dtos = restTemplate.exchange(
            baseUrl + "/api/readers",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<ReaderDto>>() {}
        ).getBody();
        return dtos.stream()
            .map(this::convertToReader)
            .collect(Collectors.toList());
    }

    public Reader getReaderById(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<ReaderDto> response = restTemplate.exchange(
            baseUrl + "/api/readers/" + id,
            HttpMethod.GET,
            entity,
            ReaderDto.class
        );
        return convertToReader(response.getBody());
    }

    public Reader createReader(Reader reader) {
        ReaderDto dto = convertToDto(reader);
        HttpEntity<ReaderDto> entity = new HttpEntity<>(dto, createHeaders());
        ResponseEntity<ReaderDto> response = restTemplate.exchange(
            baseUrl + "/api/readers",
            HttpMethod.POST,
            entity,
            ReaderDto.class
        );
        return convertToReader(response.getBody());
    }

    public Reader updateReader(Reader reader) {
        ReaderDto dto = convertToDto(reader);
        HttpEntity<ReaderDto> entity = new HttpEntity<>(dto, createHeaders());
        ResponseEntity<ReaderDto> response = restTemplate.exchange(
            baseUrl + "/api/readers/" + reader.getId(),
            HttpMethod.PUT,
            entity,
            ReaderDto.class
        );
        return convertToReader(response.getBody());
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

    private ReaderDto convertToDto(Reader reader) {
        return new ReaderDto(
            reader.getId(),
            reader.getFullName(),
            reader.getPhone(),
            reader.getEmail()
        );
    }

    private Reader convertToReader(ReaderDto dto) {
        return new Reader(
            dto.getId(),
            dto.getFullName(),
            dto.getPhone(),
            dto.getEmail()
        );
    }
} 