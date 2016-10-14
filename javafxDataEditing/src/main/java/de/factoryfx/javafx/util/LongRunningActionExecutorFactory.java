package de.factoryfx.javafx.util;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class LongRunningActionExecutorFactory<V> extends FactoryBase<LongRunningActionExecutor,V> {
    @Override
    public LiveCycleController<LongRunningActionExecutor, V> createLifecycleController() {
        return new LiveCycleController<LongRunningActionExecutor, V>() {
            @Override
            public LongRunningActionExecutor create() {
                return null;
            }
        };
    }
}
