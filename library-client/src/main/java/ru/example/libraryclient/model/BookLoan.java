package ru.example.libraryclient.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class BookLoan {
    private final LongProperty id;
    private final ObjectProperty<Book> book;
    private final ObjectProperty<Reader> reader;
    private final ObjectProperty<User> librarian;
    private final ObjectProperty<LocalDate> loanDate;
    private final ObjectProperty<LocalDate> dueDate;
    private final ObjectProperty<LocalDate> returnDate;
    private final BooleanProperty overdue;

    public BookLoan() {
        this.id = new SimpleLongProperty();
        this.book = new SimpleObjectProperty<>();
        this.reader = new SimpleObjectProperty<>();
        this.librarian = new SimpleObjectProperty<>();
        this.loanDate = new SimpleObjectProperty<>();
        this.dueDate = new SimpleObjectProperty<>();
        this.returnDate = new SimpleObjectProperty<>();
        this.overdue = new SimpleBooleanProperty();
    }

    // Геттеры и сеттеры для свойств
    public LongProperty idProperty() { return id; }
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }

    public ObjectProperty<Book> bookProperty() { return book; }
    public Book getBook() { return book.get(); }
    public void setBook(Book book) { this.book.set(book); }

    public ObjectProperty<Reader> readerProperty() { return reader; }
    public Reader getReader() { return reader.get(); }
    public void setReader(Reader reader) { this.reader.set(reader); }

    public ObjectProperty<User> librarianProperty() { return librarian; }
    public User getLibrarian() { return librarian.get(); }
    public void setLibrarian(User librarian) { this.librarian.set(librarian); }

    public ObjectProperty<LocalDate> loanDateProperty() { return loanDate; }
    public LocalDate getLoanDate() { return loanDate.get(); }
    public void setLoanDate(LocalDate loanDate) { this.loanDate.set(loanDate); }

    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }
    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }

    public ObjectProperty<LocalDate> returnDateProperty() { return returnDate; }
    public LocalDate getReturnDate() { return returnDate.get(); }
    public void setReturnDate(LocalDate returnDate) { this.returnDate.set(returnDate); }

    public BooleanProperty overdueProperty() { return overdue; }
    public boolean isOverdue() { return overdue.get(); }
    public void setOverdue(boolean overdue) { this.overdue.set(overdue); }
} 