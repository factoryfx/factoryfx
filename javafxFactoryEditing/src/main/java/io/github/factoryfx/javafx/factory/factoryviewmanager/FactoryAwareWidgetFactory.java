package io.github.factoryfx.javafx.factory.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.factory.RichClientRoot;

/**
 *
 * @param <RS> Server root
 *
 */
public abstract class FactoryAwareWidgetFactory<RS extends FactoryBase<?,RS>> extends SimpleFactoryBase<FactoryAwareWidget<RS>,RichClientRoot> {

    @Override
    public FactoryAwareWidget<RS> createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget<RS> createWidget() ;
}
