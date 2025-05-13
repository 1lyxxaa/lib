package ru.example.libraryclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;
import ru.example.libraryclient.controller.MainController;
import ru.example.libraryclient.model.User;
import ru.example.libraryclient.service.*;

@SpringBootApplication
public class LibraryApplication extends Application {
    private ConfigurableApplicationContext springContext;
    private RestTemplate restTemplate;
    private String baseUrl = "http://localhost:8080";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(LibraryApplication.class);
        restTemplate = springContext.getBean(RestTemplate.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Инициализация сервисов
            BookService bookService = new BookService(restTemplate, baseUrl);
            AuthorService authorService = new AuthorService(restTemplate, baseUrl);
            ReaderService readerService = new ReaderService(restTemplate, baseUrl);
            BookLoanService bookLoanService = new BookLoanService(restTemplate, baseUrl);

            // Загрузка главного окна
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ru/example/libraryclient/main.fxml"));
            BorderPane rootLayout = loader.load();

            // Настройка главного контроллера
            MainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setServices(bookService, authorService, readerService, bookLoanService);

            // Создание временного пользователя (в реальном приложении здесь должна быть авторизация)
            User currentUser = new User();
            currentUser.setId(1L);
            currentUser.setUsername("admin");
            currentUser.setRole("ADMIN");
            controller.setCurrentUser(currentUser);

            // Отображение сцены
            Scene scene = new Scene(rootLayout);
            primaryStage.setTitle("Библиотека");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Показываем список книг при запуске
            controller.handleShowBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        springContext.close();
    }
} 