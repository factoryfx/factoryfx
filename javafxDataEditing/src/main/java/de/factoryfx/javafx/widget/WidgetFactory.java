package de.factoryfx.javafx.widget;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public abstract class WidgetFactory<V> extends FactoryBase<Widget,V> {


    @Override
    public LiveCycleController<Widget, V> createLifecycleController() {
        return new LiveCycleController<Widget, V>() {
            @Override
            public Widget create() {
                return createWidget();
            }
        };
    }

    protected abstract Widget createWidget() ;
}
