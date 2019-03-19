package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

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
