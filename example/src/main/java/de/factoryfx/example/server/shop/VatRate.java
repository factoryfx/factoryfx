package de.factoryfx.example.server.shop;

public class VatRate  {
    private final double rate;

    public VatRate(double rate) {
        this.rate=rate;
    }

    public double calcTotalPrice(double price){
        return price+price*rate;
    }
}
