package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.List;
import ru.example.libraryclient.dto.BookLoanDto;

public class BookLoanService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private String authToken;

    public BookLoanService(RestTemplate restTemplate, String baseUrl) {
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

    public List<BookLoanDto> getAllBookLoans() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<BookLoanDto>> response = restTemplate.exchange(
            baseUrl + "/api/book-loans",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<BookLoanDto>>() {}
        );
        return response.getBody();
    }

    public BookLoanDto getBookLoanById(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<BookLoanDto> response = restTemplate.exchange(
            baseUrl + "/api/book-loans/" + id,
            HttpMethod.GET,
            entity,
            BookLoanDto.class
        );
        return response.getBody();
    }

    public BookLoanDto createBookLoan(Long bookId, Long readerId, Long librarianId, LocalDate dueDate) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<BookLoanDto> response = restTemplate.exchange(
            baseUrl + "/api/book-loans?bookId=" + bookId +
            "&readerId=" + readerId +
            "&librarianId=" + librarianId +
            "&dueDate=" + dueDate,
            HttpMethod.POST,
            entity,
            BookLoanDto.class
        );
        return response.getBody();
    }

    public BookLoanDto returnBook(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<BookLoanDto> response = restTemplate.exchange(
            baseUrl + "/api/book-loans/" + id + "/return",
            HttpMethod.PUT,
            entity,
            BookLoanDto.class
        );
        return response.getBody();
    }

    public List<BookLoanDto> getOverdueBookLoans() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<BookLoanDto>> response = restTemplate.exchange(
            baseUrl + "/api/book-loans/overdue",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<BookLoanDto>>() {}
        );
        return response.getBody();
    }

    public List<BookLoanDto> getReaderBookLoans(Long readerId) {
        ResponseEntity<List<BookLoanDto>> response = restTemplate.exchange(
            baseUrl + "/api/book-loans/reader/" + readerId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookLoanDto>>() {}
        );
        return response.getBody();
    }

    public List<BookLoanDto> getLibrarianBookLoans(Long librarianId) {
        ResponseEntity<List<BookLoanDto>> response = restTemplate.exchange(
            baseUrl + "/api/book-loans/librarian/" + librarianId,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BookLoanDto>>() {}
        );
        return response.getBody();
    }
} 