package io.github.factoryfx.example.server.shop.netherlands;

import io.github.factoryfx.factory.attribute.primitive.DoubleAttribute;
import io.github.factoryfx.example.server.shop.Product;
import io.github.factoryfx.example.server.shop.ProductFactory;

public class NetherlandsCarProductFactory extends ProductFactory {
    //special tax for cars
    public final DoubleAttribute bpmTax=new DoubleAttribute().en("BPM-Tax");

    @Override
    protected Product createImpl() {
        return new NetherlandsCarProduct(name.get(), price.get(), vatRate.instance(),bpmTax.get());
    }
}
