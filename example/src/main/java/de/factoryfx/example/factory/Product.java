package de.factoryfx.example.factory;

import de.factoryfx.factory.LiveObject;

public class Product implements LiveObject {
    private final String name;
    private final int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
