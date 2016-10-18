package de.factoryfx.javafx.distribution;

import java.io.File;

import de.factoryfx.javafx.distribution.launcher.ui.UserInterfaceDistributionClient;
import de.factoryfx.javafx.distribution.launcher.ui.UserInterfaceDistributionClientController;
import de.factoryfx.javafx.distribution.server.UserInterfaceDistributionServer;
import de.factoryfx.javafx.distribution.server.rest.DownloadResource;
import de.factoryfx.server.SinglePrecessInstanceUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SinglePrecessInstanceUtil.enforceSingleProzessInstance(12344);

        int port = 13678;
        UserInterfaceDistributionServer server = new UserInterfaceDistributionServer("localhost", port,new DownloadResource(new File("./build/distributions/UserInterface-0.1.0_0.zip")));
        new Thread(){
            @Override
            public void run() {
                server.start();
            }
        }.start();

        new UserInterfaceDistributionClient(new UserInterfaceDistributionClientController("http://localhost:13678"),primaryStage).show();
    }
}
