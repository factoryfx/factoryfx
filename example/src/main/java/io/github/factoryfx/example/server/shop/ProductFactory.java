package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.example.server.ServerBaseFactory;

public class ProductFactory extends ServerBaseFactory<Product> {
    public ProductFactory(){
        this.config().setDisplayTextProvider(name::get,name);
    }

    public final StringAttribute name = new StringAttribute().en("Name").de("Name");
    public final IntegerAttribute price = new IntegerAttribute().labelText("Price").addonText("EUR");
    public final FactoryReferenceAttribute<ServerRootFactory,VatRate,VatRateFactory> vatRate = new FactoryReferenceAttribute<ServerRootFactory,VatRate,VatRateFactory>().labelText("VatRate");

    @Override
    public Product createImpl() {
        return new Product(name.get(), price.get(), vatRate.instance());
    }

}
