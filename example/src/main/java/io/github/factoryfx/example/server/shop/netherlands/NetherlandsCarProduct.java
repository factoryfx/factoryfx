package io.github.factoryfx.example.server.shop.netherlands;

import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.VatRate;

public class NetherlandsCarProduct extends Product {
    //Private motor vehicle and motorcycle tax
    //Bpm is a one-off tax that must be paid when a car, motorcycle or light goods vehicle is registered in the Netherlands for the first time.
    private final double bpmTax;

    public NetherlandsCarProduct(String name, double price, VatRate vatRate, double bpmTax) {
        super(name, price, vatRate);
        this.bpmTax = bpmTax;
    }

    @Override
    public double getPrice() {
        return vatRate.calcTotalPrice(price)+(price*this.bpmTax);
    }
}
