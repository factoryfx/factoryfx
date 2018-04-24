package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.rest.MicroserviceResourceFactory;
import de.factoryfx.servlet.example.RootFactory;

public class FactoryfxServletContextListenerImpl extends ApplicationServerStartingServletContextListener {
    @Override
    protected Microservice<? super ServletContextAwareVisitor, ? extends FactoryBase<?, ? super ServletContextAwareVisitor,?>,?> createFactoryFxApplicationServer() {
        RootFactory rootFactory = new RootFactory();
        rootFactory.stringAttribute.set("blub");
        MicroserviceResourceFactory<ServletContextAwareVisitor, RootFactory,Void> microServiceResourceFactory = new MicroserviceResourceFactory<>();
        ApplicationServerRestServletBridgeFactory<RootFactory,Void> bridgeFactory = new ApplicationServerRestServletBridgeFactory<>();
        bridgeFactory.applicationServerResource.set(microServiceResourceFactory);
        rootFactory.applicationServerRestBridge.set(bridgeFactory);
        return new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler(new AllOrNothingFactoryExceptionHandler())),new InMemoryDataStorage<>(rootFactory));
    }
}
