package ru.example.libraryclient.model;

import javafx.beans.property.*;

public class Reader {
    private final LongProperty id;
    private final StringProperty fullName;
    private final StringProperty phone;
    private final StringProperty email;

    public Reader() {
        this.id = new SimpleLongProperty();
        this.fullName = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
    }

    public Reader(Long id, String fullName, String phone, String email) {
        this.id = new SimpleLongProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
    }

    // Геттеры и сеттеры для свойств
    public LongProperty idProperty() { return id; }
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }

    public StringProperty fullNameProperty() { return fullName; }
    public String getFullName() { return fullName.get(); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }

    public StringProperty phoneProperty() { return phone; }
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }

    public StringProperty emailProperty() { return email; }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
} 