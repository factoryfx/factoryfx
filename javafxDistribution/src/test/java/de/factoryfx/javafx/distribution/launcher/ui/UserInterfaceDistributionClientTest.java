package de.factoryfx.javafx.distribution.launcher.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class UserInterfaceDistributionClientTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        new UserInterfaceDistributionClient(new UserInterfaceDistributionClientController("https://intranet.scoop-gmbh.de/abacus/update"),primaryStage).show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}