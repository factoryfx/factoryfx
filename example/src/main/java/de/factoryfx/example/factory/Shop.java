package de.factoryfx.example.factory;

import java.util.List;

import de.factoryfx.factory.LiveObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Shop  implements LiveObject {
    private final Integer port;
    private final String host;
    private final List<Product> products;

    public Shop(Integer port, String host, List<Product> products) {
        this.port = port;
        this.host = host;
        this.products = products;
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

        TableView<Product> productTableView = new TableView<>();
        TableColumn<Product, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue().getName()));
        productTableView.getColumns().add(name);
        TableColumn<Product, String> price = new TableColumn<>("Price");
        price.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue().getPrice()));
        productTableView.getColumns().add(price);
        root.setCenter(productTableView);
        productTableView.getItems().addAll(products);
    }

    @Override
    public void stop() {

    }
}
