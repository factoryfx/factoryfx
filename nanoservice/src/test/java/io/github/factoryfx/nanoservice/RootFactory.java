package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryReferenceAttribute<RootFactory,SubscriptionStorage,SubscriptionStorageFactory> storageFactory = new FactoryReferenceAttribute<>();

    @Override
    public Root createImpl() {
        return new Root(storageFactory.instance());
    }
}
