package io.github.factoryfx.factory.exception;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;

import java.util.List;

public class ExceptionResponseAction<L,R extends FactoryBase<L,R>> {

    private final FactoryManager<L,R> factoryManager;
    private final RootFactoryWrapper<R> currentFactoryRoot;

    public ExceptionResponseAction(FactoryManager<L,R> factoryManager, RootFactoryWrapper<R> currentFactoryRoot) {
        this.factoryManager = factoryManager;
        this.currentFactoryRoot = currentFactoryRoot;
    }

    public void reset() {
        factoryManager.resetAfterCrash();
        //TODO use a new thread?
    }

    public void terminateApplication() {
        System.exit(0);
    }
}
