package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public abstract class FactoryAwareWidgetFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<FactoryAwareWidget<R>,V,R> {

    @Override
    public FactoryAwareWidget<R> createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget<R> createWidget() ;
}
