package ru.example.libraryclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Контроллер для управления списком авторов.
 * Предоставляет функциональность для просмотра, добавления, редактирования и удаления авторов.
 */
public class AuthorsController {
    @FXML private TableView<Author> authorTable;
    @FXML private TableColumn<Author, String> nameCol;
    @FXML private TableColumn<Author, Integer> birthYearCol;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn, closeBtn;
    @FXML private Label statusLabel;

    private ApiService apiService;
    private final ObservableList<Author> authors = FXCollections.observableArrayList();
    private String role;

    /**
     * Устанавливает сервис для работы с API.
     * @param apiService сервис для работы с API
     */
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        loadAuthors();
    }

    /**
     * Устанавливает роль пользователя.
     * @param role роль пользователя (ADMIN или USER)
     */
    public void setRole(String role) {
        this.role = role;
        if (!"ADMIN".equals(role)) {
            addBtn.setDisable(true);
            editBtn.setDisable(true);
            deleteBtn.setDisable(true);
        }
    }

    /**
     * Инициализирует контроллер.
     * Настраивает таблицу авторов и обработчики событий.
     */
    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        birthYearCol.setCellValueFactory(new PropertyValueFactory<>("birthYear"));
        authorTable.setItems(authors);
        refreshBtn.setOnAction(e -> loadAuthors());
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
        addBtn.setOnAction(e -> openAuthorForm(null));
        editBtn.setOnAction(e -> {
            Author selected = authorTable.getSelectionModel().getSelectedItem();
            if (selected != null) openAuthorForm(selected);
        });
        deleteBtn.setOnAction(e -> deleteSelectedAuthor());
    }

    /**
     * Загружает список авторов из API и отображает их в таблице.
     */
    private void loadAuthors() {
        statusLabel.setText("");
        authors.clear();
        new Thread(() -> {
            try {
                var list = apiService.getAuthors();
                javafx.application.Platform.runLater(() -> authors.addAll(list));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки авторов: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Отображает форму для добавления или редактирования автора.
     * @param author автор для редактирования или null для создания нового
     */
    private void openAuthorForm(Author author) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/author_form.fxml"));
            Parent root = loader.load();
            AuthorFormController controller = loader.getController();
            controller.setApiService(apiService);
            if (author != null) controller.setAuthor(author);
            controller.setOnSuccess(this::loadAuthors);
            Stage stage = new Stage();
            stage.setTitle(author == null ? "Добавить автора" : "Редактировать автора");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка открытия формы: " + ex.getMessage());
        }
    }

    /**
     * Удаляет выбранного автора.
     */
    private void deleteSelectedAuthor() {
        Author selected = authorTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление автора");
        alert.setHeaderText(null);
        alert.setContentText("Вы действительно хотите удалить автора '" + selected.getName() + "'?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.deleteAuthor(selected.getId());
                    javafx.application.Platform.runLater(this::loadAuthors);
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка удаления: " + ex.getMessage()));
                }
            }).start();
        }
    }
} 