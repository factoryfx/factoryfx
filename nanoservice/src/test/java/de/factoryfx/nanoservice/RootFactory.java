package de.factoryfx.nanoservice;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,RootFactory> {
    public final FactoryReferenceAttribute<SubscriptionStorage,SubscriptionStorageFactory> storageFactory = new FactoryReferenceAttribute<>(SubscriptionStorageFactory.class);

    @Override
    public Root createImpl() {
        return new Root(storageFactory.instance());
    }
}
