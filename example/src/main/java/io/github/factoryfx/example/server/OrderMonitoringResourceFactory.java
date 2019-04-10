package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.example.server.shop.OrderStorageFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryAttribute<ServerRootFactory,OrderStorage, OrderStorageFactory> orderStorage =
            new FactoryAttribute<>();

    @Override
    public OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
