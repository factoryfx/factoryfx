package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.util.IntegerAttribute;
import de.factoryfx.factory.attribute.util.StringAttribute;
import de.factoryfx.factory.validation.ObjectRequired;
import de.factoryfx.factory.validation.StringRequired;

public class ProductFactory extends FactoryBase<Product,ProductFactory> {
    {
        setDisplayTextProvider(product -> product.name.get());
    }

    public final StringAttribute name = new StringAttribute(new AttributeMetadata().labelText("Name")).validation(new StringRequired());
    public final IntegerAttribute price = new IntegerAttribute(new AttributeMetadata().labelText("Price")).validation(new ObjectRequired<>());
    public final ReferenceAttribute<VatRate,VatRateFactory> vatRate = new ReferenceAttribute<>(VatRateFactory.class,new AttributeMetadata().labelText("VatRate")).validation(new ObjectRequired<>());

    @Override
    protected Product createImp(Optional<Product> previousLiveObject) {
        return new Product(name.get(), price.get(), vatRate.instance());
    }

}
