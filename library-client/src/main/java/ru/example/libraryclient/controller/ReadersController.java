package ru.example.libraryclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.example.libraryclient.model.Reader;
import ru.example.libraryclient.service.ReaderService;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import java.io.IOException;
import ru.example.libraryclient.ApiService;
import javafx.scene.Parent;

public class ReadersController {
    @FXML private TableView<Reader> readersTable;
    @FXML private TableColumn<Reader, Long> idColumn;
    @FXML private TableColumn<Reader, String> fullNameColumn;
    @FXML private TableColumn<Reader, String> phoneColumn;
    @FXML private TableColumn<Reader, String> emailColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    private String role;

    private ReaderService readerService;
    private boolean initialized = false;
    private java.util.List<Reader> allReaders = new java.util.ArrayList<>();

    @FXML
    private void initialize() {
        if (initialized) return;
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Добавление слушателя выбора в таблице
        readersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateButtonsVisibility());
        
        // Отключаем кнопки при старте
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }
        
        initialized = true;
        
        // Если сервис уже установлен, обновляем список
        if (readerService != null) {
            refreshReaders();
        }
    }

    public void setReaderService(ReaderService readerService) {
        this.readerService = readerService;
        if (initialized && readerService != null) {
            refreshReaders();
        }
    }

    public void refreshReaders() {
        if (!initialized) {
            System.err.println("Warning: Controller not initialized yet");
            return;
        }
        
        if (readerService == null) {
            System.err.println("Error: Cannot refresh readers - ReaderService is null");
            if (statusLabel != null) {
                statusLabel.setText("Ошибка: сервис не инициализирован");
            }
            return;
        }
        
        try {
            allReaders.clear();
            allReaders.addAll(readerService.getAllReaders());
            applyFilter();
            if (statusLabel != null) {
                statusLabel.setText("");
            }
        } catch (Exception e) {
            System.err.println("Error refreshing readers: " + e.getMessage());
            if (statusLabel != null) {
                statusLabel.setText("Ошибка при загрузке читателей: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void updateButtonsVisibility() {
        Reader selected = readersTable.getSelectionModel().getSelectedItem();
        if (!"USER".equals(role)) {
            editButton.setDisable(selected == null);
        }
        deleteButton.setDisable(selected == null);
    }

    @FXML
    public void handleAddReader() {
        if (!initialized) {
            System.err.println("Error: Controller not initialized");
            return;
        }
        
        if (readerService == null) {
            System.err.println("Error: Cannot add reader - ReaderService is null");
            if (statusLabel != null) {
                statusLabel.setText("Ошибка: сервис не инициализирован");
            }
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/reader_form.fxml"));
            Parent root = loader.load();
            
            ReaderFormController controller = loader.getController();
            if (controller != null) {
                controller.setReaderService(readerService);
                controller.setOnSuccess(this::refreshReaders);
            } else {
                System.err.println("Error: Could not get ReaderFormController");
                if (statusLabel != null) {
                    statusLabel.setText("Ошибка: не удалось получить контроллер формы");
                }
                return;
            }
            
            Stage stage = new Stage();
            stage.setTitle("Добавить читателя");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error opening reader form: " + e.getMessage());
            if (statusLabel != null) {
                statusLabel.setText("Ошибка открытия формы: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditReader() {
        if (!initialized) {
            System.err.println("Error: Controller not initialized");
            return;
        }
        
        Reader selected = readersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (statusLabel != null) {
                statusLabel.setText("Выберите читателя для редактирования");
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/example/libraryclient/reader_form.fxml"));
            Parent root = loader.load();
            
            ReaderFormController controller = loader.getController();
            if (controller != null) {
                controller.setReaderService(readerService);
                controller.setReader(selected);
                controller.setOnSuccess(this::refreshReaders);
            } else {
                if (statusLabel != null) {
                    statusLabel.setText("Ошибка: не удалось получить контроллер формы");
                }
                return;
            }
            
            Stage stage = new Stage();
            stage.setTitle("Редактировать читателя");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            if (statusLabel != null) {
                statusLabel.setText("Ошибка открытия формы: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteReader() {
        if (!initialized) {
            System.err.println("Error: Controller not initialized");
            return;
        }
        
        Reader selected = readersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (statusLabel != null) {
                statusLabel.setText("Выберите читателя для удаления");
            }
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление читателя");
        alert.setContentText("Вы уверены, что хотите удалить читателя " + selected.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                readerService.deleteReader(selected.getId());
                refreshReaders();
                if (statusLabel != null) {
                    statusLabel.setText("Читатель успешно удален");
                }
            } catch (Exception e) {
                if (statusLabel != null) {
                    statusLabel.setText("Ошибка при удалении: " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
    }

    private void applyFilter() {
        String filter = searchField.getText().trim().toLowerCase();
        if (filter.isEmpty()) {
            readersTable.getItems().setAll(allReaders);
        } else {
            readersTable.getItems().setAll(allReaders.stream().filter(r ->
                (r.getFullName() != null && r.getFullName().toLowerCase().contains(filter)) ||
                (r.getPhone() != null && r.getPhone().toLowerCase().contains(filter)) ||
                (r.getEmail() != null && r.getEmail().toLowerCase().contains(filter))
            ).toList());
        }
    }

    public void setRole(String role) {
        this.role = role;
        boolean canEdit = "ADMIN".equals(role) || "LIBRARIAN".equals(role);
        if (addButton != null) addButton.setDisable(!canEdit);
        if (editButton != null) editButton.setDisable(!canEdit);
        if (deleteButton != null) deleteButton.setDisable(!canEdit);
    }
} 