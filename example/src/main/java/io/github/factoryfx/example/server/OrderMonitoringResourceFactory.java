package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.example.server.shop.OrderStorageFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryReferenceAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory> orderStorage =
            new FactoryReferenceAttribute<>();

    @Override
    public OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
