package de.factoryfx.example.server.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class OrderStorage {
    public static class Order{
        public final String customerName;
        public final String productName;

        @JsonCreator
        public Order(@JsonProperty("customerName") String customerName, @JsonProperty("productName")String productName) {
            this.customerName = customerName;
            this.productName = productName;
        }
    }

    private final List<Order> orders = new ArrayList<>();

    public void storeOrder(Order order){
        orders.add(order);
    }

    public void accept(OrderCollector visitor) {
        visitor.addOrders(orders);
    }

}
