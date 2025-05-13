package ru.example.libraryclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.example.libraryclient.model.User;
import ru.example.libraryclient.service.AuthorService;
import ru.example.libraryclient.service.BookLoanService;
import ru.example.libraryclient.service.BookService;
import ru.example.libraryclient.service.ReaderService;
import ru.example.libraryclient.ApiService;

public class MainController {
    private Stage primaryStage;
    private BookService bookService;
    private AuthorService authorService;
    private ReaderService readerService;
    private BookLoanService bookLoanService;
    private User currentUser;
    private ApiService apiService;

    @FXML private StackPane contentArea;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setServices(BookService bookService, AuthorService authorService, ReaderService readerService, BookLoanService bookLoanService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.readerService = readerService;
        this.bookLoanService = bookLoanService;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setAuth(String token, String role) {
        if (this.currentUser == null) {
            this.currentUser = new User();
        }
        this.currentUser.setRole(role);
        // Если нужно, можно сохранить токен в поле
        // this.token = token;
    }

    // Здесь могут быть методы для загрузки разных экранов (книги, авторы, читатели, выдачи и т.д.)

    @FXML
    public void handleShowBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/books.fxml"));
            Parent booksView = loader.load();
            Object controller = loader.getController();
            // Если BooksController, передаем сервисы/ApiService/роль пользователя
            if (controller instanceof ru.example.libraryclient.BooksController booksController) {
                if (apiService != null) booksController.setApiService(apiService);
                if (currentUser != null) booksController.setUserRole(currentUser.getRole());
            }
            contentArea.getChildren().setAll(booksView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        if (primaryStage != null) {
            primaryStage.close();
        } else {
            System.exit(0);
        }
    }

    @FXML
    private void handleAddBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/book_form.fxml"));
            Parent formView = loader.load();
            Object controller = loader.getController();
            // Если BookFormController, передаем сервисы/ApiService
            if (controller instanceof ru.example.libraryclient.BookFormController bookFormController) {
                if (apiService != null) bookFormController.setApiService(apiService);
                // Можно добавить обработчик onSuccess для обновления списка книг, если нужно
            }
            Stage stage = new Stage();
            stage.setTitle("Добавить книгу");
            stage.setScene(new Scene(formView));
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowAuthors() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/authors.fxml"));
            Parent authorsView = loader.load();
            Object controller = loader.getController();
            // Если AuthorsController, передаем сервисы/ApiService/роль пользователя
            if (controller instanceof ru.example.libraryclient.AuthorsController authorsController) {
                if (apiService != null) authorsController.setApiService(apiService);
                if (currentUser != null) authorsController.setRole(currentUser.getRole());
            }
            contentArea.getChildren().setAll(authorsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddAuthor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/author_form.fxml"));
            Parent formView = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ru.example.libraryclient.AuthorFormController authorFormController) {
                if (apiService != null) authorFormController.setApiService(apiService);
                // Можно добавить обработчик onSuccess для обновления списка авторов
            }
            Stage stage = new Stage();
            stage.setTitle("Добавить автора");
            stage.setScene(new Scene(formView));
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowReaders() {
        if (readerService == null) {
            System.err.println("Error: ReaderService is not initialized in MainController");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/readers.fxml"));
            Parent readersView = loader.load();
            
            ReadersController controller = loader.getController();
            if (controller != null) {
                // Сначала устанавливаем сервис
                controller.setReaderService(readerService);
                
                // Затем обновляем список читателей в отдельном потоке
                javafx.application.Platform.runLater(() -> {
                    try {
                        controller.refreshReaders();
                    } catch (Exception e) {
                        System.err.println("Error refreshing readers: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                System.err.println("Error: Could not get ReadersController");
                return;
            }
            
            contentArea.getChildren().setAll(readersView);
        } catch (Exception e) {
            System.err.println("Error showing readers view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddReader() {
        if (readerService == null) {
            System.err.println("Error: ReaderService is not initialized in MainController");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/reader_form.fxml"));
            Parent formView = loader.load();
            
            ReaderFormController controller = loader.getController();
            if (controller != null) {
                controller.setReaderService(readerService);
            } else {
                System.err.println("Error: Could not get ReaderFormController");
                return;
            }
            
            Stage stage = new Stage();
            stage.setTitle("Добавить читателя");
            stage.setScene(new Scene(formView));
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error opening reader form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowBookLoans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/book_loans.fxml"));
            Parent loansView = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ru.example.libraryclient.controller.BookLoansController bookLoansController) {
                bookLoansController.setServices(bookLoanService, bookService, readerService);
                bookLoansController.setCurrentUser(currentUser);
            }
            contentArea.getChildren().setAll(loansView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            stage.initOwner(primaryStage);
            if (controller instanceof ru.example.libraryclient.controller.BookLoanFormController formController) {
                formController.setServices(bookLoanService, bookService, readerService);
                formController.setCurrentUser(currentUser);
                formController.setDialogStage(stage);
            }
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowOverdue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/overdue_loans.fxml"));
            Parent overdueView = loader.load();
            Object controller = loader.getController();
            // Если OverdueLoansController, передаем сервисы/ApiService/роль пользователя
            contentArea.getChildren().setAll(overdueView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/about.fxml"));
            Parent aboutView = loader.load();
            Stage stage = new Stage();
            stage.setTitle("О программе");
            stage.setScene(new Scene(aboutView));
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 