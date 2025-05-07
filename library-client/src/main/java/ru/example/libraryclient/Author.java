package ru.example.libraryclient;

/**
 * Класс, представляющий автора в клиентском приложении.
 * Содержит информацию о имени и годе рождения автора.
 */
public class Author {
    private Long id;
    private String name;
    private Integer birthYear;

    public Author() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }

    @Override
    public String toString() {
        return name + " (" + birthYear + ")";
    }
} 