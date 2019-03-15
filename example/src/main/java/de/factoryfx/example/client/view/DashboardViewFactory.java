package de.factoryfx.example.client.view;

import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;

public class DashboardViewFactory extends WidgetFactory {

    @Override
    protected Widget createWidget() {
        return new DashboardView();
    }
}
