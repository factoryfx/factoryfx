package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBaseFactory;

public class OrderStorageFactory extends ServerBaseFactory<OrderStorage> {
    @Override
    public OrderStorage createImpl() {
        return new OrderStorage();
    }

    public OrderStorageFactory(){

    }

}
