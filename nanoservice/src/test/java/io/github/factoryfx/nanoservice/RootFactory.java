package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryAttribute<RootFactory,SubscriptionStorage,SubscriptionStorageFactory> storageFactory = new FactoryAttribute<>();

    @Override
    public Root createImpl() {
        return new Root(storageFactory.instance());
    }
}
