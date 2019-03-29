package io.github.factoryfx.javafx.factory.widget.factory;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.factory.widget.Widget;
import io.github.factoryfx.javafx.factory.RichClientRoot;

public abstract class WidgetFactory extends SimpleFactoryBase<Widget,RichClientRoot> {

    @Override
    public Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
