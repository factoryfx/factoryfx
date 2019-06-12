package io.github.factoryfx.javafx.widget.factory;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.RichClientRoot;

public abstract class WidgetFactory extends SimpleFactoryBase<Widget,RichClientRoot> {

    @Override
    protected Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
