package io.github.factoryfx.nanoservice;

import io.github.factoryfx.factory.FactoryManager;
import io.github.factoryfx.factory.RootFactoryWrapper;
import io.github.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;

public class Nanoservice<R,L extends NanoserviceRoot<R>,RF extends NanoserviceRootFactory<R,L,RF>> {

    private final FactoryManager<L,RF> factoryManager;
    private final RF rootFactory;

    public Nanoservice(FactoryManager<L,RF> factoryManager, RF rootFactory) {
        this.factoryManager = factoryManager;
        this.rootFactory = rootFactory;
    }

    public Nanoservice(RF rootFactory) {
        this(new FactoryManager<>(new RethrowingFactoryExceptionHandler()),rootFactory);
    }

    public R run() {
        factoryManager.start(new RootFactoryWrapper<>(rootFactory));
        R result = factoryManager.getCurrentFactory().internal().getLiveObject().run();
        factoryManager.stop();

        return result;
    }

}
