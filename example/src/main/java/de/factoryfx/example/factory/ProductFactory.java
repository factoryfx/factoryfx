package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.IntegerAttribute;
import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.factory.attribute.builder.AttributeBuilder;

public class ProductFactory extends FactoryBase<Product,ProductFactory> {
    public final StringAttribute name = AttributeBuilder.string().labelText("Name").build();
    public final IntegerAttribute price = AttributeBuilder.integer().labelText("Price").build();

    @Override
    protected Product createImp(Optional<Product> previousLiveObject) {
        return new Product(name.get(),price.get());
    }
}
