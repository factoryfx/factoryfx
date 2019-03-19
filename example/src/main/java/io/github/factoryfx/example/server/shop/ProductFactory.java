package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.data.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.data.attribute.types.StringAttribute;
import io.github.factoryfx.example.server.ServerBaseFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class ProductFactory extends ServerBaseFactory<Product> {
    public ProductFactory(){
        this.config().setDisplayTextProvider(name::get,name);
    }

    public final StringAttribute name = new StringAttribute().en("Name").de("Name");
    public final IntegerAttribute price = new IntegerAttribute().labelText("Price").addonText("EUR");
    public final FactoryReferenceAttribute<VatRate,VatRateFactory> vatRate = new FactoryReferenceAttribute<>(VatRateFactory.class).labelText("VatRate");

    @Override
    public Product createImpl() {
        return new Product(name.get(), price.get(), vatRate.instance());
    }

}
