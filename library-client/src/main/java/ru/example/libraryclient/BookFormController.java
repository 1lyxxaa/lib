package ru.example.libraryclient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

import ru.example.libraryclient.Book;
import ru.example.libraryclient.Author;

/**
 * Контроллер для формы добавления/редактирования книги.
 * Управляет процессом создания и обновления информации о книгах.
 */
public class BookFormController {
    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private TextField genreField;
    @FXML private TextField pagesField;
    @FXML private ComboBox<Author> authorBox;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;

    private Book book;
    private boolean isEdit = false;
    private ApiService apiService;
    private Runnable onSuccess;

    /**
     * Устанавливает сервис для работы с API.
     * @param apiService сервис для работы с API
     */
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        loadAuthors();
    }

    /**
     * Устанавливает книгу для редактирования.
     * @param book книга для редактирования
     */
    public void setBook(Book book) {
        this.book = book;
        if (book != null) {
            isEdit = true;
            titleField.setText(book.getTitle());
            yearField.setText(book.getYear() != null ? book.getYear().toString() : "");
            genreField.setText(book.getGenre());
            pagesField.setText(book.getPages() != null ? book.getPages().toString() : "");
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
        saveBtn.setOnAction(e -> saveBook());
        cancelBtn.setOnAction(e -> ((Stage) cancelBtn.getScene().getWindow()).close());
    }

    /**
     * Загружает список авторов из API и заполняет комбобокс.
     */
    private void loadAuthors() {
        new Thread(() -> {
            try {
                List<Author> authors = apiService.getAuthors();
                javafx.application.Platform.runLater(() -> {
                    authorBox.setItems(FXCollections.observableArrayList(authors));
                    if (isEdit && book != null && book.getAuthor() != null) {
                        for (Author a : authors) {
                            if (a.getId().equals(book.getAuthor().getId())) {
                                authorBox.getSelectionModel().select(a);
                                break;
                            }
                        }
                    }
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> errorLabel.setText("Ошибка загрузки авторов: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Обрабатывает нажатие кнопки сохранения.
     * Сохраняет или обновляет информацию о книге.
     */
    private void saveBook() {
        errorLabel.setText("");
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        Author author = authorBox.getValue();
        Integer year, pages;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            pages = Integer.parseInt(pagesField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("Год и страницы должны быть числами");
            return;
        }
        if (title.isEmpty() || genre.isEmpty() || author == null) {
            errorLabel.setText("Заполните все поля");
            return;
        }
        Book newBook = isEdit && book != null ? book : new Book();
        newBook.setTitle(title);
        newBook.setYear(year);
        newBook.setGenre(genre);
        newBook.setPages(pages);
        newBook.setAuthor(author);
        if (!isEdit) {
            newBook.setAvailable(true);
        }
        new Thread(() -> {
            try {
                if (isEdit) {
                    apiService.updateBook(newBook.getId(), newBook);
                } else {
                    apiService.addBook(newBook);
                }
                javafx.application.Platform.runLater(() -> {
                    if (onSuccess != null) onSuccess.run();
                    ((Stage) saveBtn.getScene().getWindow()).close();
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> errorLabel.setText("Ошибка сохранения: " + ex.getMessage()));
            }
        }).start();
    }
} 