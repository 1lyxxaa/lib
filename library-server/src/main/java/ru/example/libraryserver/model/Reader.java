package ru.example.libraryserver.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;

    @OneToMany(mappedBy = "reader")
    private List<BookLoan> bookLoans;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<BookLoan> getBookLoans() { return bookLoans; }
    public void setBookLoans(List<BookLoan> bookLoans) { this.bookLoans = bookLoans; }
} 