package ru.example.libraryserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.libraryserver.model.User;
import ru.example.libraryserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public User createUser(ru.example.libraryserver.dto.UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());
        // Пароль по умолчанию (лучше добавить поле password в UserDto)
        user.setPasswordHash(org.springframework.security.crypto.bcrypt.BCrypt.hashpw("defaultPassword", org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, ru.example.libraryserver.dto.UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow();
        user.setPasswordHash(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(newPassword, org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean verifyPassword(Long id, String password) {
        User user = userRepository.findById(id).orElseThrow();
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(password, user.getPasswordHash());
    }
} 