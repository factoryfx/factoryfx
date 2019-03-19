package io.github.factoryfx.example.server.shop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderStorage {
    public static class Order{
        public final String customerName;
        public final String productName;
        public final double price;
        public final Date orderDate;

        @JsonCreator
        public Order(@JsonProperty("customerName") String customerName,@JsonProperty("price")double price, @JsonProperty("productName")String productName, @JsonProperty("orderDate")Date orderDate) {
            this.customerName = customerName;
            this.productName = productName;
            this.price = price;
            this.orderDate = orderDate;
        }
    }

    private final List<Order> orders = new ArrayList<>();

    public void storeOrder(Order order){
        orders.add(order);
    }

    public List<Order> getOrders() {
        return orders;
    }


}
