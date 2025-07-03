package io.github.factoryfx.javafx.distribution.launcher.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.common.base.Throwables;

public class UserInterfaceDistributionClient {

    private final UserInterfaceDistributionClientController controller;
    private final Stage stage;

    public UserInterfaceDistributionClient(UserInterfaceDistributionClientController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/io/github/factoryfx/javafx/distribution/launcher/ui/UserInterfaceDistributionClientView.fxml"));
            fxmlLoader.setController(this);

            Parent root = fxmlLoader.load();
            stage.setTitle("Launcher");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> System.exit(0));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private ComboBox<String> serverUrlList;

    @FXML
    private Button startButton;

    @FXML
    private ProgressBar progress;

    @FXML
    private TextField serverUrlInput;

    @FXML
    private VBox rootPane;

    @FXML
    void initialize() {
        serverUrlList.disableProperty().bind(Bindings.size(serverUrlList.getItems()).isEqualTo(0));

        Thread.setDefaultUncaughtExceptionHandler(
            (t, e) ->
                Platform.runLater(() -> {
                    progress.setProgress(0);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error");
                    TextArea textArea = new TextArea();
                    textArea.setText(Throwables.getStackTraceAsString(e));
                    alert.setGraphic(textArea);
                    alert.show();
                }));

        readServerList();

        startButton.setOnAction(event -> {
            progress.setProgress(-1);
            rootPane.setDisable(true);
            try {
                controller.startGui(serverUrlInput.getText(),
                                    () -> {
                                        progress.setProgress(1);
                                        rootPane.setDisable(false);
                                        if (!serverUrlList.getItems().contains(serverUrlInput.getText())) {
                                            serverUrlList.getItems().add(0, serverUrlInput.getText());
                                        }
                                        writeServerList();
                                    });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progress.setProgress(0);
                    rootPane.setDisable(false);
                });
                throw e;
            }
        });

        startButton.disableProperty().bind(serverUrlInput.textProperty().isEmpty());

        serverUrlList.getSelectionModel()
                     .selectedItemProperty()
                     .addListener((observable, oldValue, newValue) -> serverUrlInput.setText(newValue));

        if (serverUrlList.getItems().isEmpty()) {
            serverUrlInput.setText("http://");
        } else {
            serverUrlInput.setText(serverUrlList.getItems().get(0));
        }

    }

    private void readServerList() {
        File file = new File("./serverList.txt");
        if (file.exists()) {
            Path path = Paths.get(file.toURI());
            try {
                serverUrlList.getItems().addAll(java.nio.file.Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeServerList() {
        Path path = Paths.get(new File("./serverList.txt").toURI());
        try {
            java.nio.file.Files.write(path, serverUrlList.getItems(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
