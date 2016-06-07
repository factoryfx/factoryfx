package de.factoryfx.example.server;

import java.util.ArrayList;
import java.util.List;

public class OrderCollector {
    private List<OrderStorage.Order> orders=new ArrayList<>();

    public void addOrders(List<OrderStorage.Order> orders){
        this.orders.addAll(orders);
    }

    public List<OrderStorage.Order> getOrders(){
        return new ArrayList<>(orders);
    }
}
