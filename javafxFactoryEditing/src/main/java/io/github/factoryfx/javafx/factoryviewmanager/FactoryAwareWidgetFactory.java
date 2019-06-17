package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.RichClientRoot;

/**
 *
 * @param <RS> Server root
 *
 */
public abstract class FactoryAwareWidgetFactory<RS extends FactoryBase<?,RS>> extends SimpleFactoryBase<FactoryAwareWidget<RS>,RichClientRoot> {

    @Override
    protected FactoryAwareWidget<RS> createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget<RS> createWidget() ;
}
