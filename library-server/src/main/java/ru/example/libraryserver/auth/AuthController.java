package ru.example.libraryserver.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.User;
import java.util.Map;

/**
 * REST-контроллер для управления аутентификацией пользователей.
 * Предоставляет методы для регистрации, входа, выхода и получения информации о текущем пользователе.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Регистрирует нового пользователя.
     * @param req карта с логином и паролем
     * @return ResponseEntity с токеном авторизации
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        String token = authService.register(username, password);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Выполняет вход пользователя.
     * @param req карта с логином и паролем
     * @return ResponseEntity с токеном авторизации
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        String token = authService.login(username, password);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Выполняет выход пользователя.
     * @param token токен авторизации
     * @return ResponseEntity с пустым телом
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Auth-Token") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает информацию о текущем пользователе.
     * @param token токен авторизации
     * @return ResponseEntity с информацией о пользователе или ошибкой
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("X-Auth-Token") String token) {
        User user = authService.getUserByToken(token);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "role", user.getRole()
        ));
    }

    @PostMapping("/register-librarian")
    public ResponseEntity<?> registerLibrarian(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        String token = authService.registerLibrarian(username, password);
        return ResponseEntity.ok(Map.of("token", token));
    }
} 