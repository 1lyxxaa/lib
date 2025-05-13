package ru.example.libraryclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import ru.example.libraryclient.Book;
import ru.example.libraryclient.model.Reader;
import ru.example.libraryclient.model.User;
import ru.example.libraryclient.service.BookLoanService;
import ru.example.libraryclient.service.BookService;
import ru.example.libraryclient.service.ReaderService;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.Button;
import java.util.Objects;

public class BookLoanFormController {
    @FXML private ComboBox<Book> bookComboBox;
    @FXML private ComboBox<Reader> readerComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private Button cancelBtn;

    private BookLoanService bookLoanService;
    private BookService bookService;
    private ReaderService readerService;
    private User currentUser;
    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        bookComboBox.setCellFactory(lv -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle());
            }
        });
        bookComboBox.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle());
            }
        });
        readerComboBox.setCellFactory(lv -> new ListCell<Reader>() {
            @Override
            protected void updateItem(Reader item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFullName());
            }
        });
        readerComboBox.setButtonCell(new ListCell<Reader>() {
            @Override
            protected void updateItem(Reader item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFullName());
            }
        });
    }

    public void setServices(BookLoanService bookLoanService, BookService bookService, ReaderService readerService) {
        this.bookLoanService = bookLoanService;
        this.bookService = bookService;
        this.readerService = readerService;
        bookComboBox.getItems().addAll(bookService.getAllBooks().stream()
                .filter(book -> Boolean.TRUE.equals(book.getAvailable()))
                .toList());
        readerComboBox.getItems().addAll(readerService.getAllReaders());
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            return;
        }
        Book selectedBook = bookComboBox.getValue();
        Reader selectedReader = readerComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        if (selectedBook == null || selectedBook.getId() == null) {
            showAlert("Не выбрана книга или у книги нет ID");
            return;
        }
        if (selectedReader == null || Objects.isNull(selectedReader.getId())) {
            showAlert("Не выбран читатель или у читателя нет ID");
            return;
        }
        if (currentUser == null || currentUser.getId() == null) {
            showAlert("Ошибка: не определён текущий пользователь");
            return;
        }
        System.out.println("[DEBUG] bookId=" + selectedBook.getId() + ", readerId=" + selectedReader.getId() + ", librarianId=" + currentUser.getId());
        bookLoanService.createBookLoan(selectedBook.getId(), selectedReader.getId(), currentUser.getId(), dueDate);
        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        } else if (cancelBtn != null && cancelBtn.getScene() != null) {
            ((Stage) cancelBtn.getScene().getWindow()).close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (bookComboBox.getValue() == null) errorMessage += "Выберите книгу\n";
        if (readerComboBox.getValue() == null) errorMessage += "Выберите читателя\n";
        if (dueDatePicker.getValue() == null) errorMessage += "Выберите срок возврата\n";
        else if (dueDatePicker.getValue().isBefore(LocalDate.now())) errorMessage += "Срок возврата не может быть в прошлом\n";
        if (errorMessage.length() == 0) return true;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
        alert.setContentText(errorMessage);
        alert.showAndWait();
        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }
} 