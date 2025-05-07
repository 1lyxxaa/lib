package ru.example.libraryclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

/**
 * Контроллер для окна статистики.
 * Отображает статистику по книгам, включая распределение по жанрам, авторам и страницам.
 */
public class StatsController {
    @FXML private TableView<Map.Entry<String, Long>> genreTable;
    @FXML private TableColumn<Map.Entry<String, Long>, String> genreCol;
    @FXML private TableColumn<Map.Entry<String, Long>, Long> genreCountCol;
    @FXML private TableView<Map.Entry<String, Long>> authorTable;
    @FXML private TableColumn<Map.Entry<String, Long>, String> authorNameCol;
    @FXML private TableColumn<Map.Entry<String, Long>, Long> authorCountCol;
    @FXML private Label pagesStatsLabel;
    @FXML private Button closeBtn;
    @FXML private Label statusLabel;

    private ApiService apiService;

    /**
     * Устанавливает сервис для работы с API.
     * @param apiService сервис для работы с API
     */
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        loadStats();
    }

    /**
     * Инициализирует контроллер.
     * Загружает и отображает статистику.
     */
    @FXML
    public void initialize() {
        genreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));
        genreCountCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getValue()).asObject());
        authorNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));
        authorCountCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getValue()).asObject());
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
    }

    /**
     * Загружает статистику с сервера и отображает ее в графиках.
     * @throws Exception если произошла ошибка при загрузке статистики
     */
    private void loadStats() {
        statusLabel.setText("");
        new Thread(() -> {
            try {
                // Жанры
                Map<String, Long> genres = apiService.getStatsGenres();
                ObservableList<Map.Entry<String, Long>> genreList = FXCollections.observableArrayList(genres.entrySet());
                // Авторы
                Map<String, Long> authors = apiService.getStatsAuthors();
                ObservableList<Map.Entry<String, Long>> authorList = FXCollections.observableArrayList(authors.entrySet());
                // Страницы
                Map<String, Number> pages = apiService.getStatsPages();
                String pagesText = String.format("Минимум: %d, Максимум: %d, Среднее: %.1f, Всего книг: %d",
                        pages.getOrDefault("min", 0).intValue(),
                        pages.getOrDefault("max", 0).intValue(),
                        pages.getOrDefault("avg", 0.0).doubleValue(),
                        pages.getOrDefault("count", 0).intValue());
                javafx.application.Platform.runLater(() -> {
                    genreTable.setItems(genreList);
                    authorTable.setItems(authorList);
                    pagesStatsLabel.setText(pagesText);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки статистики: " + ex.getMessage()));
            }
        }).start();
    }
} 