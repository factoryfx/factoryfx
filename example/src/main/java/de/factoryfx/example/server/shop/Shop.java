package de.factoryfx.example.server.shop;

import java.util.List;

import javafx.stage.Stage;

public class Shop {
    private final String stageTitle;
    private final List<Product> products;
    private final Stage stage;
    private final OrderStorage orderStorage;

    public Shop(String stageTitle, List<Product> products, Stage stage, OrderStorage orderStorage) {
        this.stageTitle = stageTitle;
        this.products = products;
        this.stage = stage;
        this.orderStorage =orderStorage;

    }
    public OrderStorage getOrderStorage() {
        return orderStorage;
    }


    public Stage getStage() {
        return stage;
    }
}
