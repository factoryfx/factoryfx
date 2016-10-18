package de.factoryfx.javafx.distribution;

import java.io.File;

import de.factoryfx.server.SinglePrecessInstanceUtil;
import de.factoryfx.javafx.distribution.launcher.ui.UserInterfaceDistributionClient;
import de.factoryfx.javafx.distribution.server.UserInterfaceDistributionServer;
import de.factoryfx.javafx.distribution.server.rest.DownloadResource;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        SinglePrecessInstanceUtil.enforceSingleProzessInstance(12344);

        int port = 13678;
        UserInterfaceDistributionServer server = new UserInterfaceDistributionServer("localhost", port,new DownloadResource(new File("./build/distributions/UserInterface-0.1.0_0.zip")));
        new Thread(){
            @Override
            public void run() {
                server.start();
            }
        }.start();
        Application.launch(UserInterfaceDistributionClient.class);


    }
}
