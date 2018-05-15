package de.factoryfx.javafx.factory.widget.factory;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.data.widget.Widget;

public abstract class WidgetFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<Widget,V,R> {

    @Override
    public Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
