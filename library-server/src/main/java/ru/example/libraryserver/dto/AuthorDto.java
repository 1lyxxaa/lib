package ru.example.libraryserver.dto;

public class AuthorDto {
    private Long id;
    private String name;
    private Integer birthYear;

    public AuthorDto() {}

    public AuthorDto(Long id, String name, Integer birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
} 