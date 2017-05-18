package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public abstract class FactoryAwareWidgetFactory<V,R extends FactoryBase<?,V>> extends SimpleFactoryBase<FactoryAwareWidget<R>,Void> {

    @Override
    public FactoryAwareWidget<R> createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget<R> createWidget() ;
}
