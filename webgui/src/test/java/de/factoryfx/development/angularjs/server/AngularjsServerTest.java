package de.factoryfx.development.angularjs.server;

import de.factoryfx.development.WebAppViewer;
import javafx.application.Application;
import javafx.stage.Stage;

public class AngularjsServerTest extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        new WebAppViewer(primaryStage,() -> new AngularjsServer(8089,"localhost").start(),"http://localhost:8089/#/view1");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}