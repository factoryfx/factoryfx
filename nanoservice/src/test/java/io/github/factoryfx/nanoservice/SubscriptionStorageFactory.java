package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.SimpleFactoryBase;

public class SubscriptionStorageFactory extends SimpleFactoryBase<SubscriptionStorage,RootFactory> {

    @Override
    protected SubscriptionStorage createImpl() {
        return new SubscriptionStorage();
    }
}
