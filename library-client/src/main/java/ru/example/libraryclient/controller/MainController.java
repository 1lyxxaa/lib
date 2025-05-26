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
import ru.example.libraryclient.service.UserService;
import ru.example.libraryclient.ApiService;
import javafx.scene.control.Button;
import ru.example.libraryclient.UsersController;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;

public class MainController {
    private Stage primaryStage;
    private BookService bookService;
    private AuthorService authorService;
    private ReaderService readerService;
    private BookLoanService bookLoanService;
    private User currentUser;
    private ApiService apiService;
    private UserService userService;

    @FXML private StackPane contentArea;
    @FXML private Button usersBtn;
    @FXML private Button readersBtn;
    @FXML private Button bookLoansBtn;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setServices(BookService bookService, AuthorService authorService, ReaderService readerService, BookLoanService bookLoanService, UserService userService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.readerService = readerService;
        this.bookLoanService = bookLoanService;
        this.userService = userService;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (usersBtn != null) {
            usersBtn.setVisible(user != null && "ADMIN".equals(user.getRole()));
        }
        updateMenuButtonsByRole();
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setAuth(String token, String role) {
        if (this.currentUser == null) {
            this.currentUser = new User();
        }
        this.currentUser.setRole(role);
        if (usersBtn != null) {
            usersBtn.setVisible("ADMIN".equals(role));
        }
        updateMenuButtonsByRole();
    }

    public void setAuthToken(String token) {
        if (bookLoanService != null) bookLoanService.setAuthToken(token);
        if (bookService != null) bookService.setAuthToken(token);
        if (authorService != null) authorService.setAuthToken(token);
        if (readerService != null) readerService.setAuthToken(token);
        if (userService != null) userService.setAuthToken(token);
    }

    @FXML
    public void initialize() {
        if (usersBtn != null) {
            usersBtn.setVisible(currentUser != null && "ADMIN".equals(currentUser.getRole()));
        }
        updateMenuButtonsByRole();
    }

    private void updateMenuButtonsByRole() {
        String role = currentUser != null ? currentUser.getRole() : null;
        boolean isUser = "USER".equals(role);
        if (readersBtn != null) readersBtn.setDisable(isUser);
        if (bookLoansBtn != null) bookLoansBtn.setDisable(isUser);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/login.fxml"));
            Parent loginView = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Вход в систему");
            loginStage.setScene(new Scene(loginView));
            loginStage.show();

            // Закрываем текущее главное окно
            if (primaryStage != null) {
                primaryStage.close();
            } else {
                // На всякий случай, если primaryStage не установлен
                Stage stage = (Stage) contentArea.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }

            // Сбросить состояние пользователя и сервисов
            this.currentUser = null;
            this.apiService = null;
            this.bookService = null;
            this.authorService = null;
            this.readerService = null;
            this.bookLoanService = null;
        } catch (Exception e) {
            e.printStackTrace();
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
            // Если AuthorsController, передаем сервисы/роль пользователя
            if (controller instanceof ru.example.libraryclient.AuthorsController authorsController) {
                if (authorService != null) authorsController.setAuthorService(authorService);
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
                if (authorService != null) authorFormController.setAuthorService(authorService);
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
                controller.setReaderService(readerService);
                if (currentUser != null) controller.setRole(currentUser.getRole());
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
                if (currentUser != null) {
                    bookLoansController.setRole(currentUser.getRole());
                }
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

    @FXML
    private void handleShowUsers() {
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            System.err.println("Доступ к управлению пользователями только для ADMIN");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/users.fxml"));
            Parent usersView = loader.load();
            UsersController controller = loader.getController();
            if (controller != null && userService != null) {
                controller.setUserService(userService);
            }
            contentArea.getChildren().setAll(usersView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        System.out.println("[DEBUG] handleChangePassword вызван");
        if (currentUser == null) {
            System.out.println("[DEBUG] currentUser == null");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка: пользователь не определён", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Сменить пароль");
        dialog.setHeaderText("Введите текущий и новый пароль");
        ButtonType okButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Текущий пароль");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Новый пароль");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Повторите новый пароль");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Текущий пароль:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("Новый пароль:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Повторите новый пароль:"), 0, 2);
        grid.add(confirmField, 1, 2);
        grid.add(errorLabel, 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return "OK";
            }
            return null;
        });

        // Обработка кнопки OK вручную
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmField.getText();
            if (oldPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Введите текущий пароль", ButtonType.OK);
                alert.showAndWait();
                event.consume();
                return;
            }
            if (newPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Введите новый пароль", ButtonType.OK);
                alert.showAndWait();
                event.consume();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Новые пароли не совпадают", ButtonType.OK);
                alert.showAndWait();
                event.consume();
                return;
            }
        });

        dialog.showAndWait().ifPresent(result -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            try {
                Long userId = currentUser.getId();
                boolean valid = userService.verifyPassword(userId, oldPassword);
                if (!valid) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Неверный текущий пароль", ButtonType.OK);
                    alert.showAndWait();
                } else {
                    userService.changePassword(userId, newPassword);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Пароль успешно изменён!", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handleShowStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/stats.fxml"));
            Parent statsView = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ru.example.libraryclient.StatsController statsController) {
                if (apiService != null) statsController.setApiService(apiService);
            }
            contentArea.getChildren().setAll(statsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 