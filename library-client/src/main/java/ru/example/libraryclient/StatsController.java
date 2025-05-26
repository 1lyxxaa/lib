package ru.example.libraryclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;

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
    @FXML private PieChart genrePieChart;
    @FXML private BarChart<String, Number> authorBarChart;
    @FXML private BarChart<String, Number> readerBarChart;
    @FXML private TableColumn<Map.Entry<String, Long>, Void> genreColorCol;

    private ApiService apiService;
    private Map<String, javafx.scene.paint.Color> genreColorMap = new HashMap<>();

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
        // Цветовая колонка для жанров
        genreColorCol.setCellFactory(col -> new TableCell<>() {
            private final javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(16, 16);
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Map.Entry<String, Long> entry = (Map.Entry<String, Long>) getTableRow().getItem();
                    String genre = entry.getKey();
                    javafx.scene.paint.Color color = genreColorMap.getOrDefault(genre,
                        javafx.scene.paint.Color.hsb(Math.abs(genre.hashCode()) % 360, 0.6, 0.85));
                    rect.setFill(color);
                    rect.setStroke(javafx.scene.paint.Color.GRAY);
                    setGraphic(rect);
                }
            }
        });
    }

    private static String toRgbString(javafx.scene.paint.Color c) {
        return String.format("rgb(%d,%d,%d)",
            (int)(c.getRed()*255),
            (int)(c.getGreen()*255),
            (int)(c.getBlue()*255));
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
                // Читатели
                Map<String, Long> readers = apiService.getStatsReaders();
                // Страницы
                Map<String, Number> pages = apiService.getStatsPages();
                String pagesText = String.format("Минимум: %d, Максимум: %d, Среднее: %.1f, Всего книг: %d",
                        pages.getOrDefault("min", 0).intValue(),
                        pages.getOrDefault("max", 0).intValue(),
                        pages.getOrDefault("avg", 0.0).doubleValue(),
                        pages.getOrDefault("count", 0).intValue());
                // PieChart по жанрам
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                genreColorMap.clear();
                for (Map.Entry<String, Long> entry : genres.entrySet()) {
                    PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
                    pieData.add(data);
                    // Генерируем цвет по жанру
                    javafx.scene.paint.Color color = javafx.scene.paint.Color.hsb(Math.abs(entry.getKey().hashCode()) % 360, 0.6, 0.85);
                    genreColorMap.put(entry.getKey(), color);
                }
                // BarChart по авторам
                XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
                for (Map.Entry<String, Long> entry : authors.entrySet()) {
                    barSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                // BarChart по читателям
                XYChart.Series<String, Number> readerSeries = new XYChart.Series<>();
                for (Map.Entry<String, Long> entry : readers.entrySet()) {
                    readerSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                javafx.application.Platform.runLater(() -> {
                    genreTable.setItems(genreList);
                    authorTable.setItems(authorList);
                    pagesStatsLabel.setText(pagesText);
                    genrePieChart.setData(pieData);
                    genrePieChart.applyCss();
                    genrePieChart.layout();
                    // Принудительно задаём цвета секторам
                    for (PieChart.Data data : genrePieChart.getData()) {
                        javafx.scene.paint.Color color = genreColorMap.getOrDefault(data.getName(),
                            javafx.scene.paint.Color.hsb(Math.abs(data.getName().hashCode()) % 360, 0.6, 0.85));
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-pie-color: " + toRgbString(color) + ";");
                        }
                    }
                    genreTable.refresh();
                    authorBarChart.getData().clear();
                    authorBarChart.getData().add(barSeries);
                    readerBarChart.getData().clear();
                    readerBarChart.getData().add(readerSeries);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки статистики: " + ex.getMessage()));
            }
        }).start();
    }
} 