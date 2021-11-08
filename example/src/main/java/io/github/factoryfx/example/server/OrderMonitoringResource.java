package io.github.factoryfx.example.server;

import java.util.List;

import io.github.factoryfx.example.server.shop.OrderStorage;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("orderMonitoring")
public class OrderMonitoringResource {

    private final OrderStorage orderStorage;

    public OrderMonitoringResource(OrderStorage orderStorage) {
        this.orderStorage = orderStorage;
    }

    @GET
    public List<OrderStorage.Order> get(){
        return orderStorage.getOrders();

    }
}
