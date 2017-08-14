package de.factoryfx.example.factory;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.example.server.OrderStorage;

public class OrderCollector {
    private final List<OrderStorage.Order> orders=new ArrayList<>();

    public void addOrders(List<OrderStorage.Order> orders){
        this.orders.addAll(orders);
    }

    public List<OrderStorage.Order> getOrders(){
        return new ArrayList<>(orders);
    }
}
