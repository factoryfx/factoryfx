package de.factoryfx.example.server.shop;

import de.factoryfx.example.server.ServerBaseFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;


public class ShopResourceFactory extends ServerBaseFactory<ShopResource> {
    public final FactoryReferenceAttribute<OrderStorage, OrderStorageFactory> orderStorage = new FactoryReferenceAttribute<>(OrderStorageFactory.class).labelText("Order storage");
    public final FactoryReferenceListAttribute<Product, ProductFactory> products = new FactoryReferenceListAttribute<>(ProductFactory.class).labelText("Products");

    @Override
    public ShopResource createImpl() {
        return new ShopResource(products.instances(), orderStorage.instance());
    }

    public ShopResourceFactory(){
        this.config().setDisplayTextProvider(()->"Shop Resource");
    }
}
