package ru.example.libraryclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.example.libraryclient.dto.BookLoanDto;
import ru.example.libraryclient.model.User;
import ru.example.libraryclient.service.BookLoanService;
import ru.example.libraryclient.service.BookService;
import ru.example.libraryclient.service.ReaderService;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Modality;

public class BookLoansController {
    @FXML private TableView<BookLoanDto> bookLoansTable;
    @FXML private TableColumn<BookLoanDto, Long> idColumn;
    @FXML private TableColumn<BookLoanDto, String> bookColumn;
    @FXML private TableColumn<BookLoanDto, String> readerColumn;
    @FXML private TableColumn<BookLoanDto, String> loanDateColumn;
    @FXML private TableColumn<BookLoanDto, String> dueDateColumn;
    @FXML private TableColumn<BookLoanDto, String> returnDateColumn;
    @FXML private TableColumn<BookLoanDto, Boolean> overdueColumn;
    @FXML private Button returnButton;
    @FXML private Button showAllButton;
    @FXML private Button showOverdueButton;

    private BookLoanService bookLoanService;
    private BookService bookService;
    private ReaderService readerService;
    private User currentUser;
    private Stage dialogStage;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBookTitle()));
        readerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReaderFullName()));
        loanDateColumn.setCellValueFactory(cellData -> {
            LocalDate loanDate = cellData.getValue().getLoanDate();
            return new SimpleStringProperty(loanDate != null ? loanDate.toString() : "");
        });
        dueDateColumn.setCellValueFactory(cellData -> {
            LocalDate dueDate = cellData.getValue().getDueDate();
            return new SimpleStringProperty(dueDate != null ? dueDate.toString() : "");
        });
        returnDateColumn.setCellValueFactory(cellData -> {
            LocalDate returnDate = cellData.getValue().getReturnDate();
            return new SimpleStringProperty(returnDate != null ? returnDate.toString() : "");
        });
        overdueColumn.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        bookLoansTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonsVisibility());
        showAllButton.setOnAction(e -> showAllLoans());
        showOverdueButton.setOnAction(e -> showOverdueLoans());
    }

    public void setServices(BookLoanService bookLoanService, BookService bookService, ReaderService readerService) {
        this.bookLoanService = bookLoanService;
        this.bookService = bookService;
        this.readerService = readerService;
        showAllLoans();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void showAllLoans() {
        bookLoansTable.getItems().clear();
        bookLoansTable.getItems().addAll(bookLoanService.getAllBookLoans());
        showAllButton.setDisable(true);
        showOverdueButton.setDisable(false);
    }

    private void showOverdueLoans() {
        bookLoansTable.getItems().clear();
        bookLoansTable.getItems().addAll(bookLoanService.getOverdueBookLoans());
        showAllButton.setDisable(false);
        showOverdueButton.setDisable(true);
    }

    private void updateButtonsVisibility() {
        BookLoanDto selected = bookLoansTable.getSelectionModel().getSelectedItem();
        returnButton.setDisable(selected == null || selected.getReturnDate() != null);
    }

    @FXML
    private void handleAddBookLoan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/book_loan_form.fxml"));
            Parent formView = loader.load();
            Object controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Выдать книгу");
            stage.setScene(new Scene(formView));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (controller instanceof ru.example.libraryclient.controller.BookLoanFormController formController) {
                formController.setServices(bookLoanService, bookService, readerService);
                formController.setCurrentUser(currentUser);
                formController.setDialogStage(stage);
            }
            stage.showAndWait();
            // После закрытия формы обновляем список
            showAllLoans();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось открыть форму выдачи книги");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReturnBook() {
        BookLoanDto selected = bookLoansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Нет выбора");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите выдачу для возврата.");
            alert.showAndWait();
            return;
        }
        if (selected.getReturnDate() != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Книга уже возвращена");
            alert.setHeaderText(null);
            alert.setContentText("Эта книга уже возвращена.");
            alert.showAndWait();
            return;
        }
        try {
            bookLoanService.returnBook(selected.getId());
            showAllLoans();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка возврата");
            alert.setHeaderText("Не удалось вернуть книгу");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
} 