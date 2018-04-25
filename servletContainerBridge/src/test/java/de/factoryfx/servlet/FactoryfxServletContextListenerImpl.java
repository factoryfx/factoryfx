package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.rest.MicroserviceResourceFactory;
import de.factoryfx.servlet.example.RootFactory;

public class FactoryfxServletContextListenerImpl extends MicroserviceStartingServletContextListener {
    @Override
    protected Microservice<? super ServletContextAwareVisitor, ? extends FactoryBase<?, ? super ServletContextAwareVisitor,?>,?> createFactoryFxMicroservice() {
        RootFactory rootFactory = new RootFactory();
        rootFactory.stringAttribute.set("blub");
        MicroserviceResourceFactory<ServletContextAwareVisitor, RootFactory,Void> microServiceResourceFactory = new MicroserviceResourceFactory<>();
        MicroserviceRestServletBridgeFactory<RootFactory,Void> bridgeFactory = new MicroserviceRestServletBridgeFactory<>();
        bridgeFactory.microserviceResource.set(microServiceResourceFactory);
        rootFactory.microserviceRestBridge.set(bridgeFactory);
        return new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new AllOrNothingFactoryExceptionHandler())),new InMemoryDataStorage<>(rootFactory));
    }
}
