package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.example.server.shop.OrderStorage;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class DashboardView implements Widget {

    private final MicroserviceRestClient<OrderCollector,ServerRootFactory,Void> client;

    public DashboardView(MicroserviceRestClient<OrderCollector, ServerRootFactory, Void> client) {
        this.client = client;
    }

    @Override
    public Node createContent() {
        StackPane root = new StackPane();

        BorderPane content = new BorderPane();
        content.setTop(new Label("Orders:"));
        TableView<OrderStorage.Order> tableView = new TableView<>();
        TableColumn<OrderStorage.Order, String> customerName = new TableColumn<>("customerName");
        customerName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().customerName));
        tableView.getColumns().add(customerName);
        TableColumn<OrderStorage.Order, String> productName = new TableColumn<>("productName");
        productName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().productName));
        tableView.getColumns().add(productName);
        content.setCenter(tableView);
        root.getChildren().add(content);

        Button update = new Button("update");
        update.setOnAction(event -> updateTableView(tableView));
        content.setBottom(update);

        updateTableView(tableView);
        return content;
    }

    public void updateTableView(TableView<OrderStorage.Order> table){
        OrderCollector query = client.query(new OrderCollector(new ArrayList<>())).value;
        table.getItems().setAll(query.orders);
    }
}
