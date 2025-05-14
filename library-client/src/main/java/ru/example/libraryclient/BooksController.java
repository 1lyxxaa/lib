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

import ru.example.libraryclient.Book;

/**
 * Контроллер для управления списком книг.
 * Предоставляет функциональность для просмотра, добавления, редактирования и удаления книг,
 * а также фильтрации и сортировки списка.
 */
public class BooksController {
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, Integer> yearCol;
    @FXML private TableColumn<Book, String> genreCol;
    @FXML private TableColumn<Book, Integer> pagesCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, String> statusCol;
    @FXML private Button addBtn, editBtn, deleteBtn, refreshBtn;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;

    private ApiService apiService;
    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private List<Book> allBooks = new ArrayList<>();
    private String userRole;

    /**
     * Устанавливает сервис для работы с API.
     * @param apiService сервис для работы с API
     */
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        loadBooks();
    }

    /**
     * Устанавливает роль пользователя.
     * @param role роль пользователя (ADMIN или USER)
     */
    public void setUserRole(String role) {
        this.userRole = role;
        updateButtonsVisibility();
    }

    /**
     * Инициализирует контроллер.
     * Настраивает таблицу книг и обработчики событий.
     */
    @FXML
    public void initialize() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        pagesCol.setCellValueFactory(new PropertyValueFactory<>("pages"));
        authorCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getAuthorName() != null ? cellData.getValue().getAuthorName() : ""
            )
        );
        statusCol.setCellValueFactory(cellData -> {
            Boolean available = cellData.getValue().getAvailable();
            String status = (available == null || available) ? "Доступна" : "Выдана";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        bookTable.setItems(books);
        refreshBtn.setOnAction(e -> loadBooks());
        addBtn.setOnAction(e -> openBookForm(null));
        editBtn.setOnAction(e -> {
            Book selected = bookTable.getSelectionModel().getSelectedItem();
            if (selected != null) openBookForm(selected);
        });
        deleteBtn.setOnAction(e -> deleteSelectedBook());
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateButtonsVisibility());
    }

    /**
     * Загружает список книг из API и отображает их в таблице.
     */
    private void loadBooks() {
        statusLabel.setText("");
        books.clear();
        new Thread(() -> {
            try {
                var list = apiService.getBooks();
                allBooks = new ArrayList<>(list);
                javafx.application.Platform.runLater(this::applyFilter);
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки книг: " + ex.getMessage()));
            }
        }).start();
    }

    private void applyFilter() {
        if (searchField == null) return;
        String filter = searchField.getText().trim().toLowerCase();
        if (filter.isEmpty()) {
            books.setAll(allBooks);
        } else {
            books.setAll(allBooks.stream().filter(b ->
                (b.getTitle() != null && b.getTitle().toLowerCase().contains(filter)) ||
                (b.getAuthorName() != null && b.getAuthorName().toLowerCase().contains(filter)) ||
                (b.getGenre() != null && b.getGenre().toLowerCase().contains(filter)) ||
                (b.getYear() != null && b.getYear().toString().contains(filter)) ||
                (b.getPages() != null && b.getPages().toString().contains(filter)) ||
                (b.getAvailable() != null && ((b.getAvailable() ? "доступна" : "выдана").contains(filter)))
            ).toList());
        }
    }

    /**
     * Открывает форму для добавления или редактирования книги.
     * @param book книга для редактирования или null для создания новой
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
     * Обновляет видимость кнопок в зависимости от роли пользователя и выбранной книги.
     */
    private void updateButtonsVisibility() {
        boolean isAdmin = "ADMIN".equals(userRole);
        boolean hasSelection = bookTable.getSelectionModel().getSelectedItem() != null;
        addBtn.setDisable(!isAdmin);
        editBtn.setDisable(!isAdmin || !hasSelection);
        deleteBtn.setDisable(!isAdmin || !hasSelection);
    }
} 