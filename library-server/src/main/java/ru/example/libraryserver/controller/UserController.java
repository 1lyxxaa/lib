package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.User;
import ru.example.libraryserver.service.UserService;
import ru.example.libraryserver.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public List<UserDto> getAllUsers(@RequestHeader("X-Auth-Token") String token) {
        return userService.getAllUsers().stream()
            .map(user -> new UserDto(user.getId(), user.getUsername(), user.getRole()))
            .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestHeader("X-Auth-Token") String token, @RequestBody UserDto userDto) {
        User user = userService.createUser(userDto);
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @RequestBody UserDto userDto) {
        User user = userService.updateUser(id, userDto);
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
        @RequestHeader("X-Auth-Token") String token,
        @PathVariable Long id,
        @RequestBody String newPassword
    ) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verify-password")
    public ResponseEntity<?> verifyPassword(
        @RequestHeader("X-Auth-Token") String token,
        @PathVariable Long id,
        @RequestBody String password
    ) {
        boolean isValid = userService.verifyPassword(id, password);
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
} 