package ru.example.libraryclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.example.libraryclient.model.Reader;
import ru.example.libraryclient.service.ReaderService;
import javafx.scene.control.Alert;

public class ReaderFormController {
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;

    private Reader reader;
    private boolean isEdit = false;
    private ReaderService readerService;
    private Runnable onSuccess;

    public void setReaderService(ReaderService readerService) {
        this.readerService = readerService;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
        if (reader != null) {
            isEdit = true;
            fullNameField.setText(reader.getFullName());
            phoneField.setText(reader.getPhone());
            emailField.setText(reader.getEmail());
        }
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        saveBtn.setOnAction(e -> saveReader());
        cancelBtn.setOnAction(e -> ((Stage) cancelBtn.getScene().getWindow()).close());
    }

    private boolean validateInput() {
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Проверка телефона: только цифры и длина
        if (!phone.matches("\\d{7,}")) {
            showAlert("Телефон должен содержать только цифры и быть не короче 7 символов");
            return false;
        }

        // Проверка email: простая регулярка
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Введите корректный email (например, user@example.com)");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveReader() {
        if (!validateInput()) {
            return;
        }
        if (readerService == null) {
            errorLabel.setText("Ошибка: сервис не инициализирован");
            return;
        }
        
        errorLabel.setText("");
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        if (fullName.isEmpty()) {
            errorLabel.setText("ФИО не может быть пустым");
            return;
        }
        if (phone.isEmpty()) {
            errorLabel.setText("Телефон не может быть пустым");
            return;
        }
        if (email.isEmpty()) {
            errorLabel.setText("Email не может быть пустым");
            return;
        }
        
        try {
            Reader newReader = isEdit && reader != null ? reader : new Reader();
            newReader.setFullName(fullName);
            newReader.setPhone(phone);
            newReader.setEmail(email);
            
            if (isEdit) {
                readerService.updateReader(newReader);
            } else {
                readerService.createReader(newReader);
            }
            
            if (onSuccess != null) onSuccess.run();
            ((Stage) saveBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            errorLabel.setText("Ошибка при сохранении: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        saveReader();
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }
} 