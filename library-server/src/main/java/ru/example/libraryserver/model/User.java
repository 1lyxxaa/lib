package ru.example.libraryserver.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "password_hash")
    private String password;

    private String role; // Например, "ADMIN" или "USER"
    private String email;

    @OneToMany(mappedBy = "librarian")
    private List<BookLoan> bookLoans;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<BookLoan> getBookLoans() { return bookLoans; }
    public void setBookLoans(List<BookLoan> bookLoans) { this.bookLoans = bookLoans; }
} 