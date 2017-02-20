package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryManager;

public class ExceptionResponseAction {

    private final FactoryManager factoryManager;

    public ExceptionResponseAction(FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public void destroyAll() {
        //TODO avoid exception over in destroy
//        factoryManager.stop();
    }

    public void terminateApplication() {
        System.exit(0);
    }
}
