package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.example.server.shop.OrderStorage;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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




        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Orders");
        xAxis.setLabel("Order Value");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSS");
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                return dateFormat.format(new Date(object.longValue()));
            }
            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        xAxis.setAutoRanging(true);

        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Orders");

        Button update = new Button("update");
        update.setOnAction(event -> updateLineChart(lineChart));
        content.setBottom(update);


        updateLineChart(lineChart);
        return lineChart;
    }

    public void updateLineChart(LineChart<Number,Number> lineChart){
        OrderCollector query = client.query(new OrderCollector(new ArrayList<>())).value;

        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName("Orders");
        for (OrderStorage.Order order: query.orders){
            series.getData().add(new XYChart.Data<>(order.orderDate.getTime(), order.price,order.productName));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setAutoRanging(false);
        query.orders.stream().map(o->o.orderDate.getTime()).min(Long::compare).ifPresent(value -> xAxis.setLowerBound((double)value));
        query.orders.stream().map(o->o.orderDate.getTime()).max(Long::compare).ifPresent(value -> xAxis.setUpperBound((double)value));
        xAxis.setTickUnit(1000);

//        lineChart.getXAxis().set
//        lineChart.getYAxis().requestAxisLayout();
    }
}
