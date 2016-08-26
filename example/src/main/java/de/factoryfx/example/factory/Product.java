package de.factoryfx.example.factory;

import de.factoryfx.factory.LiveObject;


public class Product implements LiveObject {
    private final String name;
    protected final double price;//in real world you should use BigDecimal for money, just to keep it simple
    protected final VatRate vatRate;

    public Product(String name, double price, VatRate vatRate) {
        this.name = name;
        this.vatRate = vatRate;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return vatRate.calcTotalPrice(price);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void accept(Object visitor) {
        //nothing
    }
}
