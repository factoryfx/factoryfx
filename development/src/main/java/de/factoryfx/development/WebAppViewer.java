package de.factoryfx.development;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebAppViewer{
    private Runnable serverCreator;
    private String startUrl;
    private Stage primaryStage;

    public WebAppViewer(Stage primaryStage, Runnable serverCreator, String startUrl){
        this.serverCreator= serverCreator;
        this.startUrl=startUrl;
        this.primaryStage=primaryStage;

        BorderPane root = new BorderPane();
        this.primaryStage.setScene(new Scene(root,1250,900));

        WebView webView = new WebView();
        root.setCenter(webView);

        this.serverCreator.run();
//        new Thread(serverCreator).start();
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            JSObject window = (JSObject) webView.getEngine().executeScript("window");
            JavaBridge bridge = new JavaBridge();
            window.setMember("java", bridge);
            webView.getEngine().executeScript("console.error = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
            webView.getEngine().executeScript("console.log = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
            webView.getEngine().executeScript("console.info = function(message)\n" +
                    "{\n" +
                    "    java.log(message);\n" +
                    "};");
        });

        webView.getEngine().load(this.startUrl);

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> System.exit(0));

        Button showInBrowser = new Button("show in browser");
        showInBrowser.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URL(this.startUrl).toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        Button refresh = new Button("refresh");
        refresh.setOnAction(event -> {
            webView.getEngine().reload();
            webView.getEngine().load("about:blank");
            webView.getEngine().load(startUrl);
        });
        HBox buttons = new HBox(3);
        buttons.getChildren().addAll(showInBrowser,refresh);
        root.setTop(buttons);


    }

    public static class JavaBridge {
        public void log(String text) {
            System.out.println(text);
        }
    }


}