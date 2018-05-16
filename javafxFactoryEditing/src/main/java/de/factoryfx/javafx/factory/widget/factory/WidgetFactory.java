package de.factoryfx.javafx.factory.widget.factory;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.RichClientRoot;

public abstract class WidgetFactory extends SimpleFactoryBase<Widget,Void,RichClientRoot> {

    @Override
    public Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
