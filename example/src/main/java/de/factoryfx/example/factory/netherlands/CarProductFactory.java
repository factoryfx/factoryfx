package de.factoryfx.example.factory.netherlands;

import java.util.Optional;

import de.factoryfx.example.factory.Product;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.DoubleAttribute;


public class CarProductFactory extends ProductFactory {
    //specila tax for cars
    public final DoubleAttribute bpmTax=new DoubleAttribute(new AttributeMetadata().en("BPM-Steuer"));


    @Override
    protected Product createImp(Optional<Product> previousLiveObject) {
        return new CarProduct(name.get(), price.get(), vatRate.instance(),bpmTax.get());
    }
}
