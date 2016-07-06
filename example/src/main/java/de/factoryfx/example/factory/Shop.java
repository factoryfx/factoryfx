package de.factoryfx.example.factory;

import java.util.List;

import de.factoryfx.example.server.OrderCollector;
import de.factoryfx.example.server.OrderStorage;
import de.factoryfx.factory.LiveObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Shop  implements LiveObject<OrderCollector> {
    private final String stageTitle;
    private final List<Product> products;
    private final Stage stage;
    private final OrderStorage orderStorage;

    public Shop(String stageTitle, List<Product> products, Stage stage, OrderStorage orderStorage) {
        this.stageTitle = stageTitle;
        this.products = products;
        this.stage = stage;
        this.orderStorage =orderStorage;
    }

    public OrderStorage getOrderStorage() {
        return orderStorage;
    }

    @Override
    public void start() {
        Platform.runLater(()->{
            BorderPane root = new BorderPane();
            stage.setScene(new Scene(root,1000,800));

            stage.setTitle(stageTitle);

            TableView<Product> productTableView = new TableView<>();
            productTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableColumn<Product, String> name = new TableColumn<>("Name");
            name.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue().getName()));
            productTableView.getColumns().add(name);
            TableColumn<Product, String> price = new TableColumn<>("Price");
            price.setCellValueFactory(param -> new SimpleStringProperty(""+param.getValue().getPrice()));
            productTableView.getColumns().add(price);
            root.setCenter(productTableView);
            productTableView.getItems().addAll(products);

            HBox buyPane = new HBox(3);
            buyPane.setAlignment(Pos.CENTER_LEFT);
            buyPane.setPadding(new Insets(3));
            buyPane.getChildren().add(new Label("user name"));
            TextField customerName = new TextField();
            buyPane.getChildren().add(customerName);
            Button buy = new Button("buy");
            buy.setOnAction(event -> {
                orderStorage.storeOrder(new OrderStorage.Order(customerName.getText(),productTableView.getSelectionModel().getSelectedItem().getName()));
            });
            buy.disableProperty().bind(productTableView.getSelectionModel().selectedItemProperty().isNull().or(customerName.textProperty().isEmpty()));
            buyPane.getChildren().add(buy);
            root.setBottom(buyPane);

            stage.show();
        });
    }

    @Override
    public void stop() {
        Platform.runLater(() -> stage.hide());

    }

    @Override
    public void accept(OrderCollector visitor) {
        visitor.addOrders(orderStorage.getAllOrders());
    }

    public Stage getStage() {
        return stage;
    }
}
