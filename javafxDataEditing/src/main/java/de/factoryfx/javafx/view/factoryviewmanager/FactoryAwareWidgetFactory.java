package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.SimpleFactoryBase;

public abstract class FactoryAwareWidgetFactory extends SimpleFactoryBase<FactoryAwareWidget,Void> {

    @Override
    public FactoryAwareWidget createImpl() {
        return createWidget();
    }

    protected abstract FactoryAwareWidget createWidget() ;
}
