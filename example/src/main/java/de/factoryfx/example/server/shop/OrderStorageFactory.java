package de.factoryfx.example.server.shop;

import de.factoryfx.example.server.ServerBaseFactory;

public class OrderStorageFactory extends ServerBaseFactory<OrderStorage> {
    @Override
    public OrderStorage createImpl() {
        return new OrderStorage();
    }

    OrderStorageFactory(){
        this.configLiveCycle().setRuntimeQueryExecutor((orderCollector, orderStorage) -> orderStorage.accept(orderCollector));
    }

}
