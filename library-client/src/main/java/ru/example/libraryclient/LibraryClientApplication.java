package ru.example.libraryclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Главный класс клиентского приложения библиотеки.
 * Запускает JavaFX приложение и отображает окно входа.
 */
public class LibraryClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Вход в справочную систему библиотеки");
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 