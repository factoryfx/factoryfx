package de.factoryfx.javafx.factory.util;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.factory.RichClientRoot;

public class LongRunningActionExecutorFactory extends SimpleFactoryBase<LongRunningActionExecutor,RichClientRoot> {
    @Override
    public LongRunningActionExecutor createImpl() {
        return new LongRunningActionExecutor();
    }
}
