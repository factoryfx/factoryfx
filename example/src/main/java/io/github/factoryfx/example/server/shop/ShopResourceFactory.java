package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBaseFactory;
import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;


public class ShopResourceFactory extends ServerBaseFactory<ShopResource> {
    public final FactoryReferenceAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory> orderStorage = new FactoryReferenceAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory>().labelText("Order storage");
    public final FactoryReferenceListAttribute<ServerRootFactory,Product, ProductFactory> products = new FactoryReferenceListAttribute<ServerRootFactory,Product, ProductFactory>().labelText("Products");

    @Override
    public ShopResource createImpl() {
        return new ShopResource(products.instances(), orderStorage.instance());
    }

    public ShopResourceFactory(){
        this.config().setDisplayTextProvider(()->"Shop Resource");
    }
}
