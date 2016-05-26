package de.factoryfx.development;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebAppViewer{
    Runnable serverCreator;
    String startUrl;
    Stage primaryStag;

    public WebAppViewer(Stage primaryStage, Runnable serverCreator, String startUrl){
        this.serverCreator= serverCreator;
        this.startUrl=startUrl;


        BorderPane root = new BorderPane();
        primaryStage.setScene(new Scene(root,1200,700));

        WebView webView = new WebView();
        root.setCenter(webView);

        new Thread(serverCreator).start();

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
        });

        webView.getEngine().load(startUrl);

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> System.exit(0));

        Button showInBrowser = new Button("show in browser");
        showInBrowser.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URL(startUrl).toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        root.setTop(showInBrowser);
    }

    public class JavaBridge {
        public void log(String text) {
            System.out.println(text);
        }
    }


}