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
import ru.example.libraryclient.service.AuthorService;
import ru.example.libraryclient.dto.AuthorDto;
import java.util.List;

/**
 * Контроллер для управления списком авторов.
 * Предоставляет функциональность для просмотра, добавления, редактирования и удаления авторов.
 */
public class AuthorsController {
    @FXML private TableView<AuthorDto> authorTable;
    @FXML private TableColumn<AuthorDto, String> nameCol;
    @FXML private TableColumn<AuthorDto, Integer> birthYearCol;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn, closeBtn;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;

    private AuthorService authorService;
    private final ObservableList<AuthorDto> authors = FXCollections.observableArrayList();
    private java.util.List<AuthorDto> allAuthors = new java.util.ArrayList<>();
    private String role;

    /**
     * Устанавливает сервис для работы с API.
     * @param authorService сервис для работы с API
     */
    public void setAuthorService(AuthorService authorService) {
        this.authorService = authorService;
        loadAuthors();
    }

    /**
     * Устанавливает роль пользователя.
     * @param role роль пользователя (ADMIN или USER)
     */
    public void setRole(String role) {
        this.role = role;
        if ("ADMIN".equals(role)) {
            addBtn.setDisable(false);
            editBtn.setDisable(false);
            deleteBtn.setDisable(false);
        } else {
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
            AuthorDto selected = authorTable.getSelectionModel().getSelectedItem();
            if (selected != null) openAuthorForm(selected);
        });
        deleteBtn.setOnAction(e -> deleteSelectedAuthor());
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }
    }

    private void applyFilter() {
        String filter = searchField.getText().trim().toLowerCase();
        if (filter.isEmpty()) {
            authors.setAll(allAuthors);
        } else {
            authors.setAll(allAuthors.stream().filter(a ->
                (a.getName() != null && a.getName().toLowerCase().contains(filter)) ||
                (a.getBirthYear() != null && a.getBirthYear().toString().contains(filter))
            ).toList());
        }
    }

    /**
     * Загружает список авторов из API и отображает их в таблице.
     */
    private void loadAuthors() {
        statusLabel.setText("");
        authors.clear();
        new Thread(() -> {
            try {
                List<AuthorDto> list = authorService.getAllAuthors();
                allAuthors = new java.util.ArrayList<>(list);
                javafx.application.Platform.runLater(() -> {
                    applyFilter();
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки авторов: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Отображает форму для добавления или редактирования автора.
     * @param author автор для редактирования или null для создания нового
     */
    private void openAuthorForm(AuthorDto author) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/author_form.fxml"));
            Parent root = loader.load();
            AuthorFormController controller = loader.getController();
            controller.setAuthorService(authorService);
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
        AuthorDto selected = authorTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление автора");
        alert.setHeaderText(null);
        alert.setContentText("Вы действительно хотите удалить автора '" + selected.getName() + "'?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new Thread(() -> {
                try {
                    authorService.deleteAuthor(selected.getId());
                    javafx.application.Platform.runLater(this::loadAuthors);
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка удаления: " + ex.getMessage()));
                }
            }).start();
        }
    }
} 