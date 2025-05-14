package ru.example.libraryserver.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;
import jakarta.annotation.PostConstruct;
import ru.example.libraryserver.repository.UserRepository;
import ru.example.libraryserver.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для управления аутентификацией пользователей.
 * Предоставляет методы для регистрации, входа, выхода и получения информации о пользователе по токену.
 */
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    // token -> User
    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    /**
     * Инициализирует администратора, если он не существует.
     */
    @PostConstruct
    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            String hash = BCrypt.hashpw("12345", BCrypt.gensalt());
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(hash);
            admin.setRole("ADMIN");
            admin.setEmail("admin@admin.com");
            userRepository.save(admin);
        }
    }

    /**
     * Регистрирует нового пользователя.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return токен авторизации
     * @throws RuntimeException если пользователь уже существует
     */
    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hash);
        user.setRole("USER");
        user.setEmail(username + "@mail.com");
        userRepository.save(user);
        return login(username, password);
    }

    /**
     * Регистрирует нового библиотекаря.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return токен авторизации
     * @throws RuntimeException если пользователь уже существует
     */
    public String registerLibrarian(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hash);
        user.setRole("LIBRARIAN");
        user.setEmail(username + "@mail.com");
        userRepository.save(user);
        return login(username, password);
    }

    /**
     * Выполняет вход пользователя.
     * @param username логин пользователя
     * @param password пароль пользователя
     * @return токен авторизации
     * @throws RuntimeException если пользователь не найден или пароль неверный
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    /**
     * Выполняет выход пользователя.
     * @param token токен авторизации
     */
    public void logout(String token) {
        sessions.remove(token);
    }

    /**
     * Возвращает пользователя по токену авторизации.
     * @param token токен авторизации
     * @return пользователь или null, если токен недействителен
     */
    public User getUserByToken(String token) {
        return sessions.get(token);
    }
} 