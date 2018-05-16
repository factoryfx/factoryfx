package de.factoryfx.example.server.shop;

public class Product {
    private final String name;
    protected final double price;//in real world you should use BigDecimal for money, double used just to keep it simple
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

}
