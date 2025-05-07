package ru.example.libraryclient;

import java.net.http.*;
import java.net.URI;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Сервис для взаимодействия с API сервера библиотеки.
 * Предоставляет методы для работы с книгами, авторами, статистикой и аутентификацией.
 */
public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private String token;

    public ApiService() {}
    public ApiService(String token) { this.token = token; }

    /**
     * Возвращает список всех книг.
     * @return список книг
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public List<Book> getBooks() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/books"))
                .GET();
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Book>>(){});
    }

    /**
     * Добавляет новую книгу.
     * @param book данные книги
     * @return созданная книга
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Book addBook(Book book) throws Exception {
        String json = mapper.writeValueAsString(book);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/books"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Book.class);
    }

    /**
     * Удаляет книгу по ее идентификатору.
     * @param id идентификатор книги
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public void deleteBook(long id) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/books/" + id))
                .DELETE();
        if (token != null) builder.header("X-Auth-Token", token);
        client.send(builder.build(), HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Обновляет существующую книгу.
     * @param id идентификатор книги
     * @param book новые данные книги
     * @return обновленная книга
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Book updateBook(long id, Book book) throws Exception {
        String json = mapper.writeValueAsString(book);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/books/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Book.class);
    }

    /**
     * Возвращает список всех авторов.
     * @return список авторов
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public List<Author> getAuthors() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/authors"))
                .GET();
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Author>>(){});
    }

    /**
     * Добавляет нового автора.
     * @param author данные автора
     * @return созданный автор
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Author addAuthor(Author author) throws Exception {
        String json = mapper.writeValueAsString(author);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/authors"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Author.class);
    }

    /**
     * Удаляет автора по его идентификатору.
     * @param id идентификатор автора
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public void deleteAuthor(long id) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/authors/" + id))
                .DELETE();
        if (token != null) builder.header("X-Auth-Token", token);
        client.send(builder.build(), HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Обновляет существующего автора.
     * @param id идентификатор автора
     * @param author новые данные автора
     * @return обновленный автор
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Author updateAuthor(long id, Author author) throws Exception {
        String json = mapper.writeValueAsString(author);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/authors/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Author.class);
    }

    /**
     * Возвращает статистику по жанрам книг.
     * @return карта с количеством книг по жанрам
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Map<String, Long> getStatsGenres() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/stats/genres"))
                .GET();
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<Map<String, Long>>(){});
    }

    /**
     * Возвращает статистику по авторам книг.
     * @return карта с количеством книг по авторам
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Map<String, Long> getStatsAuthors() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/stats/authors"))
                .GET();
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<Map<String, Long>>(){});
    }

    /**
     * Возвращает статистику по страницам книг.
     * @return карта с минимальным, максимальным, средним количеством страниц и общим количеством книг
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public Map<String, Number> getStatsPages() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/stats/pages"))
                .GET();
        if (token != null) builder.header("X-Auth-Token", token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<Map<String, Number>>(){});
    }

    /**
     * Класс, представляющий результат аутентификации.
     */
    public static class AuthResult {
        public String token;
        public String role;
    }

    /**
     * Выполняет вход пользователя.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return результат аутентификации
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public AuthResult login(String username, String password) throws Exception {
        String json = mapper.writeValueAsString(Map.of("username", username, "password", password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException(response.body());
        String token = mapper.readTree(response.body()).get("token").asText();
        // Получаем роль
        HttpRequest meReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/me"))
                .header("X-Auth-Token", token)
                .GET().build();
        HttpResponse<String> meResp = client.send(meReq, HttpResponse.BodyHandlers.ofString());
        String role = mapper.readTree(meResp.body()).get("role").asText();
        AuthResult result = new AuthResult();
        result.token = token;
        result.role = role;
        return result;
    }

    /**
     * Регистрирует нового пользователя.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return результат аутентификации
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    public AuthResult register(String username, String password) throws Exception {
        String json = mapper.writeValueAsString(Map.of("username", username, "password", password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException(response.body());
        String token = mapper.readTree(response.body()).get("token").asText();
        // Получаем роль
        HttpRequest meReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/me"))
                .header("X-Auth-Token", token)
                .GET().build();
        HttpResponse<String> meResp = client.send(meReq, HttpResponse.BodyHandlers.ofString());
        String role = mapper.readTree(meResp.body()).get("role").asText();
        AuthResult result = new AuthResult();
        result.token = token;
        result.role = role;
        return result;
    }
} 