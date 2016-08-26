package de.factoryfx.example.factory;

import de.factoryfx.factory.LiveObject;

public class VatRate implements LiveObject<OrderCollector> {
    private final double rate;

    public VatRate(double rate) {
        this.rate=rate;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void accept(OrderCollector visitor) {

    }

    public double calcTotalPrice(double price){
        return price+price*rate;
    }
}
