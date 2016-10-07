package de.factoryfx.javafx.distribution.launcher.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserInterfaceDistributionClient extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/de/factoryfx/javafx/distribution/ui/UserInterfaceDistributionClientView.fxml"));
        fxmlLoader.setController(new UserInterfaceDistributionClientController());
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        primaryStage.setTitle("Launcher");

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }
}
