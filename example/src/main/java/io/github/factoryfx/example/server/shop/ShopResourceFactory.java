package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBaseFactory;
import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;


public class ShopResourceFactory extends ServerBaseFactory<ShopResource> {
    public final FactoryAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory> orderStorage = new FactoryAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory>().labelText("Order storage");
    public final FactoryListAttribute<ServerRootFactory,Product, ProductFactory> products = new FactoryListAttribute<ServerRootFactory,Product, ProductFactory>().labelText("Products");

    @Override
    protected ShopResource createImpl() {
        return new ShopResource(products.instances(), orderStorage.instance());
    }

    public ShopResourceFactory(){
        this.config().setDisplayTextProvider(()->"Shop Resource");
    }
}
