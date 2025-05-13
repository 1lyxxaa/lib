package ru.example.libraryclient.model;

import javafx.beans.property.*;
import ru.example.libraryclient.Author;

public class Book {
    private final LongProperty id;
    private final StringProperty title;
    private final ObjectProperty<Author> author;
    private final IntegerProperty year;
    private final StringProperty genre;
    private final IntegerProperty pages;
    private final BooleanProperty available;

    public Book() {
        this.id = new SimpleLongProperty();
        this.title = new SimpleStringProperty();
        this.author = new SimpleObjectProperty<>();
        this.year = new SimpleIntegerProperty();
        this.genre = new SimpleStringProperty();
        this.pages = new SimpleIntegerProperty();
        this.available = new SimpleBooleanProperty(true);
    }

    // Геттеры и сеттеры для свойств
    public LongProperty idProperty() { return id; }
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }

    public StringProperty titleProperty() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }

    public ObjectProperty<Author> authorProperty() { return author; }
    public Author getAuthor() { return author.get(); }
    public void setAuthor(Author author) { this.author.set(author); }

    public IntegerProperty yearProperty() { return year; }
    public int getYear() { return year.get(); }
    public void setYear(int year) { this.year.set(year); }

    public StringProperty genreProperty() { return genre; }
    public String getGenre() { return genre.get(); }
    public void setGenre(String genre) { this.genre.set(genre); }

    public IntegerProperty pagesProperty() { return pages; }
    public int getPages() { return pages.get(); }
    public void setPages(int pages) { this.pages.set(pages); }

    public BooleanProperty availableProperty() { return available; }
    public boolean getAvailable() { return available.get(); }
    public void setAvailable(boolean available) { this.available.set(available); }
} 