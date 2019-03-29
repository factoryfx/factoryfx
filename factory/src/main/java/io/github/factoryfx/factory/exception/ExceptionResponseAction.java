package io.github.factoryfx.factory.exception;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.server.Microservice;

import java.util.List;

public class ExceptionResponseAction<L,R extends FactoryBase<L,R>> {

    private final FactoryManager<L,R> factoryManager;
    private final RootFactoryWrapper<R> previousFactoryRootCopy;
    private final RootFactoryWrapper<R> currentFactoryRoot;
    private final List<FactoryBase<?,?>> removed;

    public ExceptionResponseAction(FactoryManager<L,R> factoryManager, RootFactoryWrapper<R> previousFactoryRootCopy, RootFactoryWrapper<R> currentFactoryRoot, List<FactoryBase<?,?>> removed) {
        this.factoryManager = factoryManager;
        this.previousFactoryRootCopy = previousFactoryRootCopy;
        this.currentFactoryRoot = currentFactoryRoot;
        this.removed = removed;
    }

    public void reset() {
        Microservice<?, R, ?> microservice = currentFactoryRoot.getRoot().utility().getMicroservice();
        previousFactoryRootCopy.getRoot().internal().setMicroservice(microservice);

        for (FactoryBase<?,?> removedFactory : removed) {
            removedFactory.internal().cleanUpAfterCrash();
        }
        factoryManager.resetAfterCrash();

        factoryManager.start(previousFactoryRootCopy);
        //TODO use a new thread?
    }

    public void terminateApplication() {
        System.exit(0);
    }
}
