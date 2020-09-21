package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBaseFactory;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.DefaultPossibleValueProvider;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class ShopResourceFactory extends ServerBaseFactory<ShopResource> {
    public final FactoryAttribute<OrderStorage, OrderStorageFactory> orderStorage = new FactoryAttribute<OrderStorage, OrderStorageFactory>().labelText("Order storage");
    public final FactoryListAttribute<Product, ProductFactory> products = new FactoryListAttribute<Product, ProductFactory>().labelText("Products");

    @Override
    protected ShopResource createImpl() {
        return new ShopResource(products.instances(), orderStorage.instance());
    }

    public ShopResourceFactory(){
        this.config().setDisplayTextProvider(()->"Shop Resource");
        this.products.newValuesProvider(root->
                root.internal().collectChildrenDeep().stream().
                    filter(f->f.getClass()==ProductFactory.class).map(f->(ProductFactory)f).collect(Collectors.toList())
        );

    }
}
