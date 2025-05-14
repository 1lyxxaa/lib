package ru.example.libraryclient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import ru.example.libraryclient.dto.AuthorDto;
import ru.example.libraryclient.service.AuthorService;

/**
 * Контроллер для формы добавления/редактирования автора.
 * Управляет процессом создания и обновления информации об авторах.
 */
public class AuthorFormController {
    @FXML private TextField nameField;
    @FXML private TextField birthYearField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;

    private AuthorDto author;
    private boolean isEdit = false;
    private AuthorService authorService;
    private Runnable onSuccess;

    /**
     * Устанавливает сервис для работы с API.
     * @param authorService сервис для работы с API
     */
    public void setAuthorService(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Устанавливает автора для редактирования.
     * @param author автор для редактирования
     */
    public void setAuthor(AuthorDto author) {
        this.author = author;
        if (author != null) {
            isEdit = true;
            nameField.setText(author.getName());
            birthYearField.setText(author.getBirthYear() != null ? author.getBirthYear().toString() : "");
        }
    }

    /**
     * Устанавливает обработчик успешного сохранения.
     * @param onSuccess обработчик, вызываемый после успешного сохранения
     */
    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        saveBtn.setOnAction(this::handleSave);
        cancelBtn.setOnAction(this::handleCancel);
    }

    /**
     * Обрабатывает нажатие кнопки сохранения.
     * Сохраняет или обновляет информацию об авторе.
     * @param event событие нажатия кнопки
     */
    @FXML
    private void handleSave(ActionEvent event) {
        errorLabel.setText("");
        String name = nameField.getText().trim();
        Integer birthYear;
        try {
            birthYear = Integer.parseInt(birthYearField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("Год рождения должен быть числом");
            return;
        }
        if (name.isEmpty()) {
            errorLabel.setText("Имя не может быть пустым");
            return;
        }
        AuthorDto newAuthor = isEdit && author != null ? author : new AuthorDto();
        newAuthor.setName(name);
        newAuthor.setBirthYear(birthYear);
        new Thread(() -> {
            try {
                if (isEdit) {
                    authorService.updateAuthor(newAuthor);
                } else {
                    authorService.createAuthor(newAuthor);
                }
                javafx.application.Platform.runLater(() -> {
                    if (onSuccess != null) onSuccess.run();
                    closeWindow();
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> errorLabel.setText("Ошибка сохранения: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Обрабатывает нажатие кнопки отмены.
     * Закрывает окно формы.
     * @param event событие нажатия кнопки
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    /**
     * Закрывает окно формы.
     */
    private void closeWindow() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
} 