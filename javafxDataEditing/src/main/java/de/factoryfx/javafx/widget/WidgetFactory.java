package de.factoryfx.javafx.widget;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class WidgetFactory<V> extends FactoryBase<Widget,V> {


    @Override
    public LiveCycleController<Widget, V> createLifecycleController() {
        return new LiveCycleController<Widget, V>() {
            @Override
            public Widget create() {
                return null;
            }
        };
    }
}
