package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.RootFactoryWrapper;
import de.factoryfx.server.Microservice;

import java.util.List;
import java.util.function.Consumer;

public class ExceptionResponseAction<V,R extends FactoryBase<?,V,R>> {

    private final FactoryManager<V,R> factoryManager;
    private final RootFactoryWrapper<R> previousFactoryRootCopy;
    private final RootFactoryWrapper<R> currentFactoryRoot;
    private final List<FactoryBase<?,?,?>> removed;

    public ExceptionResponseAction(FactoryManager factoryManager, RootFactoryWrapper<R> previousFactoryRootCopy, RootFactoryWrapper<R> currentFactoryRoot, List<FactoryBase<?,?,?>> removed) {
        this.factoryManager = factoryManager;
        this.previousFactoryRootCopy = previousFactoryRootCopy;
        this.currentFactoryRoot = currentFactoryRoot;
        this.removed = removed;
    }

    public void reset() {
        Microservice<V, R, ?> microservice = currentFactoryRoot.getRoot().utilityFactory().getMicroservice();
        previousFactoryRootCopy.getRoot().internalFactory().setMicroservice(microservice);

        for (FactoryBase<?, ?, ?> removedFactory : removed) {
            removedFactory.internalFactory().cleanUpAfterCrash();
        }
        factoryManager.restCurrentFactory();

        factoryManager.start(previousFactoryRootCopy);
    }

    public void terminateApplication() {
        System.exit(0);
    }
}
