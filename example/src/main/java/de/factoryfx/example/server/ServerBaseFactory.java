package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.factory.SimpleFactoryBase;

public abstract class ServerBaseFactory<L> extends SimpleFactoryBase<L,OrderCollector,ServerRootFactory> {
}
