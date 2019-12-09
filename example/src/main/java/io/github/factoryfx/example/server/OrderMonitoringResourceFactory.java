package io.github.factoryfx.example.server;

import io.github.factoryfx.example.server.shop.OrderStorage;
import io.github.factoryfx.example.server.shop.OrderStorageFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.time.DurationAttribute;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryAttribute<OrderStorage, OrderStorageFactory> orderStorage = new FactoryAttribute<>();
    public final DurationAttribute attribute = new DurationAttribute();

    @Override
    protected OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
