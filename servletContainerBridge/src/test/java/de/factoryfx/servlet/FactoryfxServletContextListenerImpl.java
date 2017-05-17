package de.factoryfx.servlet;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.ApplicationServerResourceFactory;
import de.factoryfx.servlet.example.Root;
import de.factoryfx.servlet.example.RootFactory;

public class FactoryfxServletContextListenerImpl extends ApplicationServerStartingServletContextListener {
    @Override
    protected ApplicationServer<? super ServletContextAwareVisitor, ?, ? extends FactoryBase<?, ? super ServletContextAwareVisitor>> createFactoryFxApplicationServer() {
        RootFactory rootFactory = new RootFactory();
        rootFactory.stringAttribute.set("blub");
        ApplicationServerResourceFactory<ServletContextAwareVisitor, Root, RootFactory> applicationServerResourceFactory = new ApplicationServerResourceFactory<>();
        ApplicationServerRestServletBridgeFactory<Root, RootFactory> bridgeFactory = new ApplicationServerRestServletBridgeFactory<>();
        bridgeFactory.applicationServerResource.set(applicationServerResourceFactory);
        rootFactory.applicationServerRestBridge.set(bridgeFactory);
        return new ApplicationServer<>(new FactoryManager<>(new LoggingFactoryExceptionHandler<>(new AllOrNothingFactoryExceptionHandler<>())),new InMemoryFactoryStorage<>(rootFactory));
    }
}
