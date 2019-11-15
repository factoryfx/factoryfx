package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.example.server.shop.OrderStorageFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class OrderMonitoringResourceFactory2 extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryAttribute<OrderStorage, OrderStorageFactory> orderStorage =
            new FactoryAttribute<>();

    @Override
    protected OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
