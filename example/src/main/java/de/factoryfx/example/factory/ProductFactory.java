package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ProductFactory extends FactoryBase<Product> {
    public ProductFactory(){
        this.setDisplayTextProvider(() -> name.get());
    }

    public final StringAttribute name = new StringAttribute(new AttributeMetadata().en("Name").de("Name")).validation(new StringRequired());
    public final IntegerAttribute price = new IntegerAttribute(new AttributeMetadata().labelText("Price").addonText("EUR")).validation(new ObjectRequired<>());
    public final FactoryReferenceAttribute<VatRate,VatRateFactory> vatRate = new FactoryReferenceAttribute<>(VatRateFactory.class,new AttributeMetadata().labelText("VatRate")).validation(new ObjectRequired<>());

    @Override
    protected Product createImp(Optional<Product> previousLiveObject) {
        return new Product(name.get(), price.get(), vatRate.instance());
    }

}
