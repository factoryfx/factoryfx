package io.github.factoryfx.example.client.view;

import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;

public class DashboardViewFactory extends WidgetFactory {

    @Override
    protected Widget createWidget() {
        return new DashboardView();
    }
}
