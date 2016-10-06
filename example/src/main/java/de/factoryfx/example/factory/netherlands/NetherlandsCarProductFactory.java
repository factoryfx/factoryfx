package de.factoryfx.example.factory.netherlands;

import java.util.Optional;

import de.factoryfx.example.factory.OrderCollector;
import de.factoryfx.example.factory.Product;
import de.factoryfx.example.factory.ProductFactory;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.factory.LifecycleNotifier;

public class NetherlandsCarProductFactory extends ProductFactory {
    //specila tax for cars
    public final DoubleAttribute bpmTax=new DoubleAttribute(new AttributeMetadata().en("BPM-Steuer"));


    @Override
    protected Product createImp(Optional<Product> previousLiveObject, LifecycleNotifier<OrderCollector> lifecycle) {
        return new NetherlandsCarProduct(name.get(), price.get(), vatRate.instance(),bpmTax.get());
    }
}
