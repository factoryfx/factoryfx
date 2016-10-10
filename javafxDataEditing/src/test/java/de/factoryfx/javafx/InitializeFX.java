package de.factoryfx.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class InitializeFX {

    public static class DummyApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
        }
    }

    static {
        Thread t = new Thread() {
            @Override
            public void run() {
                Application.launch(DummyApp.class);
            }
        };
        t.setDaemon(true);
        t.start();


    }


}
