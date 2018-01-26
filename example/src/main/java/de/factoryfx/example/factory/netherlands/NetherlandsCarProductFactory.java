package de.factoryfx.example.factory.netherlands;

import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.example.factory.Product;
import de.factoryfx.example.factory.ProductFactory;

public class NetherlandsCarProductFactory extends ProductFactory {
    //special tax for cars
    public final DoubleAttribute bpmTax=new DoubleAttribute().en("BPM-Tax");

    @Override
    public Product createImpl() {
        return new NetherlandsCarProduct(name.get(), price.get(), vatRate.instance(),bpmTax.get());
    }
}
