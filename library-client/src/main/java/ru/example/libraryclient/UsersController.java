package ru.example.libraryclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import ru.example.libraryclient.dto.UserDto;
import ru.example.libraryclient.service.UserService;

public class UsersController {
    @FXML private TableView<UserDto> userTable;
    @FXML private TableColumn<UserDto, String> usernameCol;
    @FXML private TableColumn<UserDto, String> roleCol;
    @FXML private Button addBtn, deleteBtn, changeRoleBtn, closeBtn;
    @FXML private Label statusLabel;

    private UserService userService;
    private final ObservableList<UserDto> users = FXCollections.observableArrayList();

    public void setUserService(UserService userService) {
        this.userService = userService;
        loadUsers();
    }

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        userTable.setItems(users);
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
        addBtn.setOnAction(e -> handleAddUser());
        deleteBtn.setOnAction(e -> handleDeleteUser());
        changeRoleBtn.setOnAction(e -> handleChangeRole());
    }

    private void loadUsers() {
        statusLabel.setText("");
        users.clear();
        if (userService == null) return;
        new Thread(() -> {
            try {
                var list = userService.getAllUsers();
                javafx.application.Platform.runLater(() -> users.setAll(list));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка загрузки пользователей: " + ex.getMessage()));
            }
        }).start();
    }

    private void handleAddUser() {
        javafx.application.Platform.runLater(() -> {
            Dialog<UserDto> dialog = new Dialog<>();
            dialog.setTitle("Добавить пользователя");
            dialog.setHeaderText(null);
            ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            TextField usernameField = new TextField();
            usernameField.setPromptText("Логин");
            ComboBox<String> roleBox = new ComboBox<>();
            roleBox.getItems().addAll("USER", "LIBRARIAN", "ADMIN");
            roleBox.setValue("USER");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Логин:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Роль:"), 0, 1);
            grid.add(roleBox, 1, 1);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new UserDto(null, usernameField.getText().trim(), roleBox.getValue());
                }
                return null;
            });

            dialog.showAndWait().ifPresent(userDto -> {
                new Thread(() -> {
                    try {
                        userService.createUser(userDto);
                        javafx.application.Platform.runLater(this::loadUsers);
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка добавления: " + ex.getMessage()));
                    }
                }).start();
            });
        });
    }

    private void handleDeleteUser() {
        UserDto selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите пользователя для удаления");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление пользователя");
        alert.setHeaderText(null);
        alert.setContentText("Удалить пользователя " + selected.getUsername() + "?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        userService.deleteUser(selected.getId());
                        javafx.application.Platform.runLater(this::loadUsers);
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка удаления: " + ex.getMessage()));
                    }
                }).start();
            }
        });
    }

    private void handleChangeRole() {
        UserDto selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите пользователя для смены роли");
            return;
        }
        javafx.application.Platform.runLater(() -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Изменить роль");
            dialog.setHeaderText("Пользователь: " + selected.getUsername());
            ButtonType okButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
            ComboBox<String> roleBox = new ComboBox<>();
            roleBox.getItems().addAll("USER", "LIBRARIAN", "ADMIN");
            roleBox.setValue(selected.getRole());
            dialog.getDialogPane().setContent(roleBox);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return roleBox.getValue();
                }
                return null;
            });
            dialog.showAndWait().ifPresent(newRole -> {
                UserDto updated = new UserDto(selected.getId(), selected.getUsername(), newRole);
                new Thread(() -> {
                    try {
                        userService.updateUser(selected.getId(), updated);
                        javafx.application.Platform.runLater(this::loadUsers);
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Ошибка смены роли: " + ex.getMessage()));
                    }
                }).start();
            });
        });
    }
} 