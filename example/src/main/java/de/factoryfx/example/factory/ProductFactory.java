package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.factory.attribute.StringAttribute;

public class ProductFactory extends FactoryBase<Product,ProductFactory> {
    public final StringAttribute name = new StringAttribute(new AttributeMetadata().labelText("Name"));
    public final IntegerAttribute price = new IntegerAttribute(new AttributeMetadata().labelText("Price"));

    @Override
    protected Product createImp(Optional<Product> previousLiveObject) {
        return new Product(name.get(),price.get());
    }
}
