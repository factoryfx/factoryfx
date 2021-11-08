package io.github.factoryfx.example.client.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.javafx.widget.Widget;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

public class DashboardView implements Widget {

    public DashboardView() {
    }

    @Override
    public Node createContent() {
        BorderPane content = new BorderPane();
        content.setTop(new Label("Orders:"));

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

        content.setCenter(lineChart);
        content.setBottom(update);


        updateLineChart(lineChart);
        return content;
    }

    public void updateLineChart(LineChart<Number,Number> lineChart){
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(ObjectMapperBuilder.buildNewObjectMapper());
        ClientConfig configuration = new ClientConfig(new ClientConfig());
        configuration.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        configuration.register(jacksonProvider);
        Client client = ClientBuilder.newClient(configuration);

        GenericType<List<OrderStorage.Order>> responseType = new GenericType<>() {};
        List<OrderStorage.Order> orders = client.target("http://localhost:8089/orderMonitoring").request(MediaType.APPLICATION_JSON).get(responseType);

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
    }
}
