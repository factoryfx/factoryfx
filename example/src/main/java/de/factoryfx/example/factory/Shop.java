package de.factoryfx.example.factory;

import java.util.List;

import de.factoryfx.factory.LiveObject;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Shop  implements LiveObject {
    private final Integer port;
    private final String host;
    List<Product> products;

    public Shop(Integer port, String host, List<Product> products) {
        this.port = port;
        this.host = host;
    }

    @Override
    public void start() {
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        stage.setScene(new Scene(root,1000,800));

        TextArea textArea = new TextArea();
        textArea.setText("dummy use for parameter\n"+"port:"+port+"\n"+"host:"+host);
        root.setTop(textArea);
        stage.show();
    }

    @Override
    public void stop() {

    }
}
