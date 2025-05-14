package ru.example.libraryclient.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.dto.UserDto;
import java.util.List;

public class UserService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private String authToken;

    public UserService(RestTemplate restTemplate, String baseUrl) {
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

    public List<UserDto> getAllUsers() {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
            baseUrl + "/api/users",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<UserDto>>() {}
        );
        return response.getBody();
    }

    public UserDto createUser(UserDto user) {
        HttpEntity<UserDto> entity = new HttpEntity<>(user, createHeaders());
        ResponseEntity<UserDto> response = restTemplate.exchange(
            baseUrl + "/api/users",
            HttpMethod.POST,
            entity,
            UserDto.class
        );
        return response.getBody();
    }

    public void deleteUser(Long id) {
        HttpEntity<?> entity = new HttpEntity<>(createHeaders());
        restTemplate.exchange(
            baseUrl + "/api/users/" + id,
            HttpMethod.DELETE,
            entity,
            Void.class
        );
    }

    public UserDto updateUserRole(Long id, String newRole) {
        HttpEntity<String> entity = new HttpEntity<>(newRole, createHeaders());
        ResponseEntity<UserDto> response = restTemplate.exchange(
            baseUrl + "/api/users/" + id + "/role",
            HttpMethod.PUT,
            entity,
            UserDto.class
        );
        return response.getBody();
    }

    public UserDto updateUser(Long id, UserDto user) {
        HttpEntity<UserDto> entity = new HttpEntity<>(user, createHeaders());
        ResponseEntity<UserDto> response = restTemplate.exchange(
            baseUrl + "/api/users/" + id,
            HttpMethod.PUT,
            entity,
            UserDto.class
        );
        return response.getBody();
    }

    public void changePassword(Long id, String newPassword) {
        HttpEntity<String> entity = new HttpEntity<>(newPassword, createHeaders());
        restTemplate.exchange(
            baseUrl + "/api/users/" + id + "/password",
            HttpMethod.PUT,
            entity,
            Void.class
        );
    }

    public boolean verifyPassword(Long id, String password) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(password, createHeaders());
            ResponseEntity<?> response = restTemplate.exchange(
                baseUrl + "/api/users/" + id + "/verify-password",
                HttpMethod.POST,
                entity,
                Void.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
} 