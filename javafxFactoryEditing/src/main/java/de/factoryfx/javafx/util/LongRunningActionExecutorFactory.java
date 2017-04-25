package de.factoryfx.javafx.util;

import de.factoryfx.factory.SimpleFactoryBase;

public class LongRunningActionExecutorFactory<V> extends SimpleFactoryBase<LongRunningActionExecutor,V> {
    @Override
    public LongRunningActionExecutor createImpl() {
        return new LongRunningActionExecutor();
    }
}
