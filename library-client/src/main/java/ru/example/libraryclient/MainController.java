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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Главный контроллер клиентского приложения библиотеки.
 * Отвечает за отображение списка книг, работу с CRUD-кнопками, фильтрами, переходами к авторам, статистике и выходу из аккаунта.
 */
public class MainController {
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, Integer> yearCol;
    @FXML private TableColumn<Book, String> genreCol;
    @FXML private TableColumn<Book, Integer> pagesCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn, authorsBtn, statsBtn, aboutBtn, logoutBtn;
    @FXML private Label statusLabel;
    @FXML private TextField filterTitleField, filterGenreField, filterAuthorField, filterYearField;

    private String token;
    private String role;
    private ApiService apiService;
    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private List<Book> allBooks = new ArrayList<>();

    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        pagesCol.setCellValueFactory(new PropertyValueFactory<>("pages"));
        authorCol.setCellValueFactory(cellData -> {
            Author a = cellData.getValue().getAuthor();
            return new javafx.beans.property.SimpleStringProperty(a != null ? a.getName() : "");
        });
        bookTable.setItems(books);
        refreshBtn.setOnAction(e -> loadBooks());
        addBtn.setOnAction(e -> openBookForm(null));
        editBtn.setOnAction(e -> {
            Book selected = bookTable.getSelectionModel().getSelectedItem();
            if (selected != null) openBookForm(selected);
        });
        deleteBtn.setOnAction(e -> deleteSelectedBook());
        authorsBtn.setOnAction(e -> openAuthorsWindow());
        statsBtn.setOnAction(e -> openStatsWindow());
        aboutBtn.setOnAction(e -> openAboutWindow());
        logoutBtn.setOnAction(e -> logout());
        filterTitleField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        filterGenreField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        filterAuthorField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        filterYearField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
    }

    /**
     * Устанавливает токен и роль пользователя, инициализирует ApiService.
     * @param token токен авторизации
     * @param role роль пользователя (ADMIN/USER)
     */
    public void setAuth(String token, String role) {
        this.token = token;
        this.role = role;
        this.apiService = new ApiService(token);
        if (!"ADMIN".equals(role)) {
            addBtn.setDisable(true);
            editBtn.setDisable(true);
            deleteBtn.setDisable(true);
        }
        loadBooks();
    }

    /**
     * Загружает список книг с сервера и обновляет таблицу.
     */
    private void loadBooks() {
        statusLabel.setText("");
        books.clear();
        new Thread(() -> {
            try {
                var list = apiService.getBooks();
                allBooks = new ArrayList<>(list);
                javafx.application.Platform.runLater(this::applyFiltersAndSort);
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки книг: " + ex.getMessage()));
            }
        }).start();
    }

    private void applyFiltersAndSort() {
        String title = filterTitleField.getText().trim().toLowerCase();
        String genre = filterGenreField.getText().trim().toLowerCase();
        String author = filterAuthorField.getText().trim().toLowerCase();
        String yearStr = filterYearField.getText().trim();
        List<Book> filtered = allBooks.stream()
                .filter(b -> title.isEmpty() || b.getTitle().toLowerCase().contains(title))
                .filter(b -> genre.isEmpty() || b.getGenre().toLowerCase().contains(genre))
                .filter(b -> author.isEmpty() || (b.getAuthor() != null && b.getAuthor().getName().toLowerCase().contains(author)))
                .filter(b -> yearStr.isEmpty() || (b.getYear() != null && b.getYear().toString().contains(yearStr)))
                .collect(Collectors.toList());
        filtered.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareTo)));
        books.setAll(filtered);
    }

    /**
     * Открывает окно добавления или редактирования книги.
     * @param book книга для редактирования или null для добавления
     */
    private void openBookForm(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/book_form.fxml"));
            Parent root = loader.load();
            BookFormController controller = loader.getController();
            controller.setApiService(apiService);
            if (book != null) controller.setBook(book);
            controller.setOnSuccess(this::loadBooks);
            Stage stage = new Stage();
            stage.setTitle(book == null ? "Добавить книгу" : "Редактировать книгу");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка открытия формы: " + ex.getMessage());
        }
    }

    /**
     * Открывает окно авторов.
     */
    private void openAuthorsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/authors.fxml"));
            Parent root = loader.load();
            AuthorsController controller = loader.getController();
            controller.setApiService(apiService);
            controller.setRole(role);
            Stage stage = new Stage();
            stage.setTitle("Авторы");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка открытия окна авторов: " + ex.getMessage());
        }
    }

    /**
     * Открывает окно статистики.
     */
    private void openStatsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/stats.fxml"));
            Parent root = loader.load();
            StatsController controller = loader.getController();
            controller.setApiService(apiService);
            Stage stage = new Stage();
            stage.setTitle("Статистика");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка открытия окна статистики: " + ex.getMessage());
        }
    }

    /**
     * Открывает окно "Об авторе".
     */
    private void openAboutWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/about.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Об авторе");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка открытия окна об авторе: " + ex.getMessage());
        }
    }

    /**
     * Удаляет выбранную книгу после подтверждения пользователя.
     */
    private void deleteSelectedBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление книги");
        alert.setHeaderText(null);
        alert.setContentText("Вы действительно хотите удалить книгу '" + selected.getTitle() + "'?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.deleteBook(selected.getId());
                    javafx.application.Platform.runLater(this::loadBooks);
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка удаления: " + ex.getMessage()));
                }
            }).start();
        }
    }

    /**
     * Выход из учетной записи, возврат к окну логина.
     */
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Вход в справочную систему библиотеки");
            stage.setScene(new javafx.scene.Scene(root, 400, 250));
            stage.show();
            ((Stage) logoutBtn.getScene().getWindow()).close();
        } catch (Exception ex) {
            statusLabel.setText("Ошибка выхода: " + ex.getMessage());
        }
    }
}