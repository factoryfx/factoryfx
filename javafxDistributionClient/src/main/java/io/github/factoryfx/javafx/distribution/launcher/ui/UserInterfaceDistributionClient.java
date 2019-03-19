package io.github.factoryfx.javafx.distribution.launcher.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
            fxmlLoader.setController(controller);

            Parent root = fxmlLoader.load();
            stage.setTitle("Launcher");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnCloseRequest(event -> System.exit(0));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
