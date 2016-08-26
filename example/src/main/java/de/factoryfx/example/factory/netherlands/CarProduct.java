package de.factoryfx.example.factory.netherlands;

import de.factoryfx.example.factory.Product;
import de.factoryfx.example.factory.VatRate;

public class CarProduct extends Product {
    private final double bpmTax;

    public CarProduct(String name, double price, VatRate vatRate, double bpmTax) {
        super(name, price, vatRate);
        this.bpmTax = bpmTax;
    }

    @Override
    public double getPrice() {
        return vatRate.calcTotalPrice(price)+(price*this.bpmTax);
    }
}
