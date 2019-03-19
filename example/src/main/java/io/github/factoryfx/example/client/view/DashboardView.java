package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.javafx.data.widget.Widget;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardView implements Widget {

    public DashboardView() {
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

        Button update = new Button("edit");
        update.setOnAction(event -> updateLineChart(lineChart));
        content.setBottom(update);


        updateLineChart(lineChart);
        return lineChart;
    }

    public void updateLineChart(LineChart<Number,Number> lineChart){
        Client client = ClientBuilder.newClient();
        List<OrderStorage.Order> orders = client.target("http://localhost/orderMonitoring").request(MediaType.APPLICATION_JSON).get(new GenericType<List<OrderStorage.Order>>(){});


        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName("Orders");
        for (OrderStorage.Order order: orders){
            series.getData().add(new XYChart.Data<>(order.orderDate.getTime(), order.price,order.productName));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setAutoRanging(false);
        orders.stream().map(o->o.orderDate.getTime()).min(Long::compare).ifPresent(value -> xAxis.setLowerBound((double)value));
        orders.stream().map(o->o.orderDate.getTime()).max(Long::compare).ifPresent(value -> xAxis.setUpperBound((double)value));
        xAxis.setTickUnit(1000);

//        lineChart.getXAxis().set
//        lineChart.getYAxis().requestAxisLayout();
    }
}
