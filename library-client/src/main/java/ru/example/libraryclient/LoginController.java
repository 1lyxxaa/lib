package ru.example.libraryclient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import ru.example.libraryclient.controller.MainController;
import ru.example.libraryclient.service.*;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.model.User;

/**
 * Контроллер для окна входа в систему.
 * Управляет процессом аутентификации пользователя и переходом к главному окну приложения.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Label errorLabel;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        loginBtn.setOnAction(this::handleLogin);
        registerBtn.setOnAction(this::handleRegister);
    }

    /**
     * Обрабатывает нажатие кнопки входа.
     * Выполняет попытку входа пользователя и при успехе переходит к главному окну.
     * @param event событие нажатия кнопки
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        errorLabel.setText("");
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите логин и пароль");
            return;
        }
        new Thread(() -> {
            try {
                var result = apiService.login(username, password);
                String token = result.token;
                String role = result.role;
                apiService.setToken(token);
                javafx.application.Platform.runLater(() -> openMainWindow(token, role));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> errorLabel.setText("Ошибка входа: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Обрабатывает нажатие кнопки регистрации.
     * Выполняет регистрацию нового пользователя и при успехе переходит к главному окну.
     * @param event событие нажатия кнопки
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        errorLabel.setText("");
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите логин и пароль");
            return;
        }
        new Thread(() -> {
            try {
                var result = apiService.register(username, password);
                String token = result.token;
                String role = result.role;
                apiService.setToken(token);
                javafx.application.Platform.runLater(() -> openMainWindow(token, role));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> errorLabel.setText("Ошибка регистрации: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Отображает главное окно приложения.
     * @param token токен авторизации
     * @param role роль пользователя
     * @throws Exception если произошла ошибка при загрузке окна
     */
    private void openMainWindow(String token, String role) {
        try {
            // Инициализация сервисов
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://localhost:8080";
            
            BookService bookService = new BookService(restTemplate, baseUrl);
            AuthorService authorService = new AuthorService(restTemplate, baseUrl);
            ReaderService readerService = new ReaderService(restTemplate, baseUrl);
            BookLoanService bookLoanService = new BookLoanService(restTemplate, baseUrl);
            UserService userService = new UserService(restTemplate, baseUrl);
            
            // Установка токена для всех сервисов
            bookService.setAuthToken(token);
            authorService.setAuthToken(token);
            readerService.setAuthToken(token);
            bookLoanService.setAuthToken(token);
            userService.setAuthToken(token);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            
            // Получаем пользователя с сервера по username
            String username = usernameField.getText().trim();
            User user = apiService.getUserByUsername(username);
            // Передача сервисов и пользователя в контроллер
            controller.setServices(bookService, authorService, readerService, bookLoanService, userService);
            controller.setApiService(apiService);
            controller.setCurrentUser(user);
            controller.setAuth(token, role);
            
            Stage stage = new Stage();
            stage.setTitle("Библиотека - клиент");
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
            ((Stage) loginBtn.getScene().getWindow()).close();
        } catch (Exception ex) {
            errorLabel.setText("Ошибка открытия главного окна: " + ex.getMessage());
        }
    }
} 