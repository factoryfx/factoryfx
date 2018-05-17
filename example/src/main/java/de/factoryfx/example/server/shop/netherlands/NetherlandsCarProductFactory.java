package de.factoryfx.example.server.shop.netherlands;

import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.example.server.shop.Product;
import de.factoryfx.example.server.shop.ProductFactory;

public class NetherlandsCarProductFactory extends ProductFactory {
    //special tax for cars
    public final DoubleAttribute bpmTax=new DoubleAttribute().en("BPM-Tax");

    @Override
    public Product createImpl() {
        return new NetherlandsCarProduct(name.get(), price.get(), vatRate.instance(),bpmTax.get());
    }
}
