package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.OrderStorage;
import de.factoryfx.example.server.shop.OrderStorageFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class OrderMonitoringResourceFactory extends ServerBaseFactory<OrderMonitoringResource> {
    public final FactoryReferenceAttribute<OrderStorage, OrderStorageFactory> orderStorage =
            new FactoryReferenceAttribute<>(OrderStorageFactory.class);

    @Override
    public OrderMonitoringResource createImpl() {
        return new OrderMonitoringResource(orderStorage.instance());
    }

}
