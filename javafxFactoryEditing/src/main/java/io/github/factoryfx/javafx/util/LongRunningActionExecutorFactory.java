package io.github.factoryfx.javafx.util;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.RichClientRoot;

public class LongRunningActionExecutorFactory extends SimpleFactoryBase<LongRunningActionExecutor,RichClientRoot> {
    @Override
    protected LongRunningActionExecutor createImpl() {
        return new LongRunningActionExecutor();
    }
}
