package de.factoryfx.example.server;

import java.util.ArrayList;
import java.util.List;

public class OrderStorage {
    public static class Order{
        public String customerName;
        public String productName;

        public Order(String customerName, String productName) {
            this.customerName = customerName;
            this.productName = productName;
        }
    }

    List<Order> orders = new ArrayList<>();

    public void storeOrder(Order order){
        orders.add(order);
    }

    public List<Order> getAllOrders(){
        return new ArrayList<>(orders);
    }
}
