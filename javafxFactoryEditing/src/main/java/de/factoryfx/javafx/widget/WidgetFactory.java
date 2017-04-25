package de.factoryfx.javafx.widget;

import de.factoryfx.factory.SimpleFactoryBase;

public abstract class WidgetFactory<V> extends SimpleFactoryBase<Widget,V> {

    @Override
    public Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
