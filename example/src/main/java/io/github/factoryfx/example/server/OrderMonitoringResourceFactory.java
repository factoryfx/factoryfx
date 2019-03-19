package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.example.server.shop.OrderStorageFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryReferenceAttribute<OrderStorage, OrderStorageFactory> orderStorage =
            new FactoryReferenceAttribute<>(OrderStorageFactory.class);

    @Override
    public OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
