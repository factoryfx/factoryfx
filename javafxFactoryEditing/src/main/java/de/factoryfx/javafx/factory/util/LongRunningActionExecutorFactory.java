package de.factoryfx.javafx.factory.util;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public class LongRunningActionExecutorFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<LongRunningActionExecutor,V,R> {
    @Override
    public LongRunningActionExecutor createImpl() {
        return new LongRunningActionExecutor();
    }
}
