package de.factoryfx.example.server.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderCollector {
    public final List<OrderStorage.Order> orders;

    public OrderCollector(@JsonProperty("orders") List<OrderStorage.Order> orders) {
        this.orders=orders;
    }

    public void addOrders(List<OrderStorage.Order> orders){
        this.orders.addAll(orders);
    }

}
