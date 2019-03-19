package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryReferenceAttribute<SubscriptionStorage,SubscriptionStorageFactory> storageFactory = new FactoryReferenceAttribute<>(SubscriptionStorageFactory.class);

    @Override
    public Root createImpl() {
        return new Root(storageFactory.instance());
    }
}
