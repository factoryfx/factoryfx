package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.RootFactoryWrapper;
import de.factoryfx.server.Microservice;

import java.util.List;

public class ExceptionResponseAction<V,L,R extends FactoryBase<L,V,R>> {

    private final FactoryManager<V,L,R> factoryManager;
    private final RootFactoryWrapper<R> previousFactoryRootCopy;
    private final RootFactoryWrapper<R> currentFactoryRoot;
    private final List<FactoryBase<?,?,?>> removed;

    public ExceptionResponseAction(FactoryManager<V,L,R> factoryManager, RootFactoryWrapper<R> previousFactoryRootCopy, RootFactoryWrapper<R> currentFactoryRoot, List<FactoryBase<?,?,?>> removed) {
        this.factoryManager = factoryManager;
        this.previousFactoryRootCopy = previousFactoryRootCopy;
        this.currentFactoryRoot = currentFactoryRoot;
        this.removed = removed;
    }

    public void reset() {
        Microservice<V, ?, R, ?> microservice = currentFactoryRoot.getRoot().utilityFactory().getMicroservice();
        previousFactoryRootCopy.getRoot().internalFactory().setMicroservice(microservice);

        for (FactoryBase<?, ?, ?> removedFactory : removed) {
            removedFactory.internalFactory().cleanUpAfterCrash();
        }
        factoryManager.resetAfterCrash();

        factoryManager.start(previousFactoryRootCopy);
        //TODO use a new thread?
    }

    public void terminateApplication() {
        System.exit(0);
    }
}
