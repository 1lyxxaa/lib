package ru.example.libraryclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Контроллер для окна "О программе".
 * Отображает информацию о версии приложения и его назначении.
 */
public class AboutController {
    @FXML private Button closeBtn;
    @FXML private Label versionLabel;

    /**
     * Инициализирует контроллер.
     * Устанавливает версию приложения в метку.
     */
    @FXML
    public void initialize() {
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
        versionLabel.setText("Версия 1.0");
    }
} 