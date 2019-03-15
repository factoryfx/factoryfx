package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.OrderStorage;

import javax.ws.rs.GET;
import java.util.List;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    private final OrderStorage orderStorage;

    public OrderMonitoringResourceFactory(OrderStorage orderStorage) {
        this.orderStorage = orderStorage;
    }

    @Override
    public OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource();
    }

    @GET
    public List<OrderStorage.Order> get(){
        return orderStorage.getOrders();

    }
}
