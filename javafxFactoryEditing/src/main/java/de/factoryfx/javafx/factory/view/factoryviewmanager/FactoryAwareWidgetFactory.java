package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.factory.RichClientRoot;

/**
 *
 * @param <RS> Server root
 *
 */
public abstract class FactoryAwareWidgetFactory<RS extends FactoryBase<?,?,RS>> extends SimpleFactoryBase<FactoryAwareWidget<RS>,Void,RichClientRoot> {

    @Override
    public FactoryAwareWidget<RS> createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget<RS> createWidget() ;
}
