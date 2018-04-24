package de.factoryfx.server.nanoservice;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionStorageFactory extends SimpleFactoryBase<SubscriptionStorage,Void,RootFactory> {

    @Override
    public SubscriptionStorage createImpl() {
        return new SubscriptionStorage();
    }
}
